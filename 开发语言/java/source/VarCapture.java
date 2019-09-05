package javaDemo;

interface MyFunc {
	int func(int n);
}

public class VarCapture {
	
	public static void main(String args[])  {
		
		int num = 0;
		
		MyFunc mylambda = (n) -> {
			int v = num + n; //没有修改局部变量num
			
			//num++; //修改局部变量
			
			return v;
		};
		
		//num = 9; //也会引起错误，因为它移除num的final状态
	}
}

