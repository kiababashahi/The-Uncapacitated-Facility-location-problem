//The code for the first relaxation 
public class LR_C2 {
	int max_iter = 10000;
	int interv_dec_lambda = 10000;
	int last = 1;
	double epsilon = 40;
	double best_lower_bound = 0;
	double Lambda[][];
	double[][] c;
	double min_cost;
	double[] f;
	double[] d;
	int[] z;
	int[][] x;
	int[][] s;
	int M;
	double f_cost;
	double sqrNorm;
	int N;
	double L_value;
	int best_i;
	Huristic h;
	int[] open_facility;
	int[] customer_assign;

	public LR_C2(Read_Input r1) {//initializing in the constructor 
		c = r1.clientCost;
		f = r1.facilityCost;
		d = r1.clientDemand;
		M = r1.numbClient;
		N = r1.numbFacility;
		x = new int[N][M];
		z = new int[N];
		open_facility = new int[N];
		s = new int[N][M];
		Lambda = new double[N][M];
		h = new Huristic(r1);
	}

	public void Lagrangian_Relaxation(double upper_bound) {

		for (int t = 0; t < max_iter; t++) {

			L_value = 0;
			for (int i = 0; i < N; i++) { // Reset value of decision variables
				for (int j = 0; j < M; j++)
					x[i][j] = 0;
				z[i] = 0;
			}
			for (int j = 0; j < M; j++) {
				min_cost = d[j] * c[0][j] + Lambda[0][j];
				best_i = 0;
				for (int i = 1; i < N; i++) {
					if (d[j] * c[i][j] + Lambda[i][j] < min_cost) {
						min_cost = d[j] * c[i][j] + Lambda[i][j];
						best_i = i;
					}
				}
				x[best_i][j] = 1;
				L_value += min_cost;
			}

			for (int i = 0; i < N; i++) { // Solve ith 0-1 z_i subproblem
				f_cost = f[i];
				for (int j = 0; j < M; j++)
					f_cost -= Lambda[i][j];
				if (f_cost < 0) {
					z[i] = 1;
					L_value += f_cost;
				}
			}
			if (L_value > best_lower_bound) { // Update best lower bound
				best_lower_bound = L_value;
				for (int i = 0; i < N; i++)
					open_facility[i] = z[i];
				double UB = h.Reassign_customers(); // Lagrangean heuristic
				if (UB != -1 && UB < upper_bound)
					upper_bound = UB;
			}
			System.out.println(t+1 + ": " + " L_value: " + L_value + " " + " BLB: " + best_lower_bound + " UB: "
					+ upper_bound + " gap: " + (upper_bound - best_lower_bound) / upper_bound * 100 + "epsilon is+ " +epsilon);
			double SqrNorm = 0;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					s[i][j] = x[i][j] - z[i];
					SqrNorm += (double) (s[i][j] * (s[i][j]));
				}
			}
			// Check stoping criteria
			if ((upper_bound - best_lower_bound) / upper_bound * 100 < 0.005)
				break;
			// Compute steplength
			if (t - last > interv_dec_lambda) {
				last = t;
				epsilon = epsilon / 2;
			}
			if (epsilon < 0.0001)
				epsilon = 2;

			double StepLength = epsilon * (upper_bound - L_value) / SqrNorm;
			for (int i = 0; i < N; i++) { // Update Lagrange multipliers
				for (int j = 0; j < M; j++) {
					if (Lambda[i][j] + StepLength * s[i][j] > 0)
						Lambda[i][j] = Lambda[i][j] + StepLength * s[i][j];
					else
						Lambda[i][j] = 0;
				}
			}

		}
	}
}
