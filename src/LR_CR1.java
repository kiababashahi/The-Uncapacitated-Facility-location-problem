	//This is the code for the relaxation of the second constraint
public class LR_CR1 {
	int max_iter = 10000;
	int interv_dec_lambda = 10000;
	int last = 1;
	double epsilon = 2;
	double best_lower_bound = 0;
	double Lambda[];
	double[][] c;
	double min_cost;
	double[] f;
	double[] d;
	int[] z;
	int[][] x;
	int[] s;
	int M;
	// double f_cost;
	// double SqrNorm;
	int N;
	double L_value;
	int best_i;
	Huristic h;
	int[] open_facility;
	int[] customer_assign;
	double LamSum;
	double best_yet = 0;

	public LR_CR1(Read_Input r1) { //initializing in the constructor 
		c = r1.clientCost;
		f = r1.facilityCost;
		d = r1.clientDemand;
		M = r1.numbClient;
		N = r1.numbFacility;
		x = new int[N][M];
		z = new int[N];
		open_facility = new int[N];
		s = new int[M];
		Lambda = new double[M];
		h = new Huristic(r1);
		customer_assign = new int[M];
	}

	public void Lagrangian_Relaxation(double upper_bound) { 

		for (int t = 0; t < max_iter; t++) {
			L_value = 0;
			LamSum = 0;
			for (int i = 0; i < N; i++) { // Reset value of decision variables for each t
				for (int j = 0; j < M; j++) {
					x[i][j] = 0;
					s[j]=0;
					}
				z[i] = 0;
			}

			for (int j = 0; j < M; j++) { // calculates the sum of the duals

				LamSum += Lambda[j];
			}

			for (int i = 0; i < N; i++) { // solves each of the i subproblems
				customer_assign = new int[M];
				min_cost = 0;
				for (int j = 0; j < M; j++) {
					if (d[j] * c[i][j] + Lambda[j] < 0) {
						min_cost += (d[j] * c[i][j] + Lambda[j]);
						customer_assign[j] = 1;
					}
				}
				
				if (f[i] + min_cost < 0) {
					z[i] = 1;
					L_value += (f[i] + min_cost);
					for (int j = 0; j < M; j++) {
						x[i][j] = customer_assign[j];
					}
				} else {
					for (int j = 0; j < M; j++)
						x[i][j] = 0;
				}

			}
			
			L_value = L_value - LamSum;
			if (L_value > best_lower_bound) { // Update best lower bound
				best_lower_bound = L_value;
				for (int i = 0; i < N; i++)
					open_facility[i] = z[i];
				double UB = h.Reassign_customers(); // Lagrangian heuristic
				if (UB != -1 && UB < upper_bound)
					upper_bound = UB;
			}
			System.out.println(t+1 +": "+ " L_value: " + L_value + " " + " BLB: " + best_lower_bound + " UB: " + upper_bound
					+ " gap: " + (upper_bound - best_lower_bound) / upper_bound * 100 + "epsilon is+ " +epsilon);
			double SqrNorm = 0;
			for (int j = 0; j < M; j++) {
				for (int i = 0; i < N; i++) {
					s[j] += x[i][j];
				}
				
				s[j] -= 1;
				SqrNorm += (double) (s[j] * s[j]);
				

			}

			// Check stopping criteria
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
			
			// Update Lagrange multipliers
			for (int j = 0; j < M; j++) {
				Lambda[j] = Lambda[j] + StepLength * s[j];
			}
		}
	
	}
}
