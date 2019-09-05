package javaDemo;

interface StringFunc {
	String func(String n);
}

public class lambdasAsArgumentsDemo {
	
	//第一个参数是函数式接口
	static String stringOp(StringFunc sf, String s) {
		return sf.func(s);
	}
	
	public static void main(String args[]) {
		
		String inStr = "lambdas add power to Java";
		String outStr;
		
		System.out.println("Here is input String: " + inStr);
		
		outStr = stringOp((str) -> str.toUpperCase(), inStr);
		System.out.println("The string in uppercase: " + outStr);
		
		//移除空白的lambda块
		outStr = stringOp((str) -> {
				String result = "";
				int i;
				
				for(i = 0; i<str.length(); i++)
				if(str.charAt(i) != ' ')
					result += str.charAt(i);
				
				return result;			
			}, inStr);
		
		System.out.println("The string with space removed: " + outStr);
		
		StringFunc reverse = (str) -> {
			String result = "";
			int i;
			
			for(i=str.length()-1; i>=0; i--) {
				result += str.charAt(i);
			}
			return result;
		};
		
		System.out.println("The String reversed: " + stringOp(reverse, inStr));
	}
}
