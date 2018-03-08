
public class Huristic {
	double[][] c;
	double[]f;
	double[]d;
	int[]b;
	double current_obj;
	int best_assign[];
	int imp_assign[];
	int M;
	double f_cost;	double MAX_DOUBLE=10000000000.0;
	int N;
	int[]open_facility;
	int[]customer_assign;
	public Huristic(Read_Input r1) {
		c=r1.clientCost;
		f=r1.facilityCost;
		d=r1.clientDemand;
		M=r1.numbClient;
		N=r1.numbFacility;
		best_assign=new int[M];
		imp_assign=new int[M];
		open_facility=new int[N];
		customer_assign=new int[M];
	}
	public void Print_solution()
	{
	 int i;
	 System.out.println("Set of open facilities: ");
	 for(i=0;i<N;i++){
	   if(open_facility[i] == 1)
		 System.out.println(i+1);
	 }
	}
	double Local_Search_Heuristic(double obj)
	{
	current_obj=obj;
	 int flag = 1;
	 while(flag==1) {
	   flag = Close_Facility();
	   if(flag == 0){
	     flag = Open_Facility();
		 if(flag == 0)
		   flag = Open_Close_Facility();
	   }
	 }

	 return current_obj;
	}
	int Close_Facility(){
	 int i;
	 double cost;
	 int best_facility = -1;

	 for(i=0;i<N;i++){
	   if(open_facility[i] == 1){
		 open_facility[i] = 0;
		 cost = Reassign_customers();
		 if(cost < current_obj){
		   current_obj = cost;
		   best_facility = i;
		 }
		 open_facility[i] = 1;
	   }
	  }

	   if(best_facility != -1){
	     open_facility[best_facility] = 0;
	    // printf("Improved solution found in Close_Facility Neighbourhood \n Objectie value: %.2f \n",current_obj);
	    // Print_solution();
	     return 1;
	   }else
	     return 0;
	 }
	int Open_Facility()
	{
	 int i;
	 double cost;
	 int best_facility = -1;

	 for(i=0;i<N;i++){
	   if(open_facility[i] == 0){
		 open_facility[i] = 1;
		 cost = Reassign_customers();
		 if(cost < current_obj){
		   current_obj = cost;
		   best_facility = i;
		 }
		 open_facility[i] = 0;
	   }
	 }

	 if(best_facility != -1){
	   open_facility[best_facility] = 1;
	 //  printf("Improved solution found in Open_Facility Neighbourhood \n Objectie value: %.2f \n",current_obj);
	 //  Print_solution();
	   return 1;
	 }else
	   return 0;
	}
	int Open_Close_Facility()
	{
	 int i1,i2;
	 double cost;
	 int best_open = -1;
	 int best_closed = -1;

	 for(i1=0;i1<N;i1++){
	   if(open_facility[i1] == 0){
	     for(i2=0;i2<N;i2++){
	       if(open_facility[i2] == 1){
		     open_facility[i1] = 1;
			 open_facility[i2] = 0;
		     cost = Reassign_customers();
		     if(cost < current_obj){
		       current_obj = cost;
		       best_open = i1;
			   best_closed = i2;
		     }
		     open_facility[i1] = 0;
			 open_facility[i2] = 1;
		   }
		 }
	   }
	 }

	 if(best_open != -1 && best_closed != -1){
	   open_facility[best_open] = 1;
	   open_facility[best_closed] = 0;
	  //  printf("Improved solution found in Open_Close_Facility Neighbourhood \n Objectie value: %.2f \n",current_obj);
	  // Print_solution();
	   return 1;
	 }else
	   return 0;
	}

	public double Reassign_customers()
	{
	 int i,j;
	 double cost, assign_cost;
	 int flag = 0;

	 for(j=0;j<M;j++){          //Assign each customer to its closest open facility
	   customer_assign[j] = -1;
	   assign_cost = MAX_DOUBLE;
	   for(i=0;i<N;i++){
		 if(open_facility[i] == 1 && c[i][j] < assign_cost){
		   assign_cost = c[i][j];
		   customer_assign[j] = i;
		 }
	   }
	 }
	 
	 cost = 0;                  // Compute opbjective value of current solution 
	 for(i=0;i<N;i++){
	   if(open_facility[i] == 1){
	     cost += f[i];
		 flag = 1;
	   } 
	 }
	 if(flag == 1){
	   for(j=0;j<M;j++)
	     cost += d[j]*c[customer_assign[j]][j];
	   return cost;
	 }
	 else 
	   return -1;
	}
	double Constructive_Heuristic()	{
	 int i,j;
	 int stoping_criteria, best_facility;
	 double cost, best_cost, imp_cost;
	 int count=1;
	 best_cost = MAX_DOUBLE;
	 for(i=0;i<N;i++)        //Initialize solution (empty solution = no factilities open)
	   open_facility[i] = 0;
	 for(j=0;j<M;j++)
		 best_assign[j] = -1;

	 do{
	  
	   best_facility = -1;
	   imp_cost = best_cost;

	   for(i=0;i<N;i++){
	     if(open_facility[i] == 0){   //Evaluate the benefit of opening a temporarily new facility and reassigning customers
		   open_facility[i] = 1;
		   cost = Reassign_customers();	                        
		 //  printf("New objective value when adding facility %d to current solution: \t %10.2f \n",i+1,cost);
		   if(cost < imp_cost){       //Compare cost to best found solution so far
			 best_facility = i;
			 imp_cost = cost;
			 for(j=0;j<M;j++)
			  imp_assign[j] = customer_assign[j];
		   }

		   open_facility[i] = 0;      //Close facility that was temporarily open
		 }
	   }
	                               //If a new best solution is found, update current solution and continue
	   if(best_facility != -1){
		   best_cost = imp_cost;
		   open_facility[best_facility] = 1;
		   for(j=0;j<M;j++)
			 best_assign[j] = imp_assign[j];
		   stoping_criteria = 0;
	   }else
		   stoping_criteria = 1;

	 }while(stoping_criteria != 1);
	 return best_cost;
	}
	}
