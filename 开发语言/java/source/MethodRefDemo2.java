package javaDemo;

//一个字符串操作的函数接口
interface StringFunc {
	String func(String n);
}

class MyStringOps {

	String strReverse(String str) {
		String result = "";
		int i;

		for (i = str.length() - 1; i >= 0; i--)
			result += str.charAt(i);

		return result;
	}

}

public class MethodRefDemo2 {
	
	static String stringOp(StringFunc sf, String s){
		return sf.func(s);
	}

	public static void main(String args[]) {
		
		String inStr = "lambdas add power to Java";
		String outStr;
		
		MyStringOps strOps = new MyStringOps();
		
		outStr = stringOp(strOps::strReverse, inStr);
		
		System.out.println("Original string: " + inStr);
		System.out.println("String reversed: " + outStr);
	}
}
