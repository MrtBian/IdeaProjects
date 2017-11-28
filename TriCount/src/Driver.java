import java.util.Random;

public class Driver {
	public static void main(String[] args)throws Exception{
		String temp1="OTC-temp-1"+ Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
		String input1[]={args[0],temp1};
		OrStep1.main(input1);
		String temp2="OTC-temp-2" + Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
		String input2[]={temp1,temp2};
		OrStep2.main(input2);
		String input3[]={temp2,args[1]};
		OrTriangleCount.main(input3);
		
	}
}
