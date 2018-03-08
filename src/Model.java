
public class Model {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Read_Input r=new Read_Input();
		r.readFile("capb.txt");
		LR_C2 Relaxation1=new LR_C2(r);
		LR_CR1 Relaxation2=new LR_CR1(r);
		Huristic h=new Huristic(r);
		long startHeutime = System.currentTimeMillis();
		double obj_value_ch=h.Constructive_Heuristic();
		double obj_value_ls = h.Local_Search_Heuristic(obj_value_ch);
		long endHeutime = System.currentTimeMillis();
		long Heu_dur = (endHeutime - startHeutime);
		//h.Print_solution();
		long LINEARstartTime1 = System.currentTimeMillis();
		Optimal_Cplex c1=new Optimal_Cplex(r);
		double s=c1.solve();
		long LINEARendTime1 = System.currentTimeMillis();
		long LINEARduration1 = (LINEARendTime1 - LINEARstartTime1);
		System.out.println("cplex time:" +LINEARduration1 );
		long start_first = System.currentTimeMillis();
		Relaxation1.Lagrangian_Relaxation(s);
		long end_first = System.currentTimeMillis();
		long first_dur = (start_first - end_first);
		
		long start_second = System.currentTimeMillis();
		//Relaxation2.Lagrangian_Relaxation(s);
		long end_second = System.currentTimeMillis();
		long second_dur = (end_second - start_second);
		//System.out.println(s);
		//System.out.println("the optimal value is: "+s+ " and it took:" +LINEARduration1+ "mili secs");
		//System.out.println("thehEU val is : "+obj_value_ls+ " and it took:" +Heu_dur+ "mili secs");
		//System.out.println("the accuracy of the huristic is:" + (obj_value_ls-s));
	//	System.out.println("the first relaxation is solved in"+ first_dur+" mili sec");
		//System.out.println("the second relaxation is solved in "+ second_dur+" mili sec");
	}

}
