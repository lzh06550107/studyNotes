package javaDemo;

interface MyFunc {
	MyClass func(int n);
}

class MyClass {
	private int val;
	
	MyClass(int v) { val = v; }
	
	MyClass() { val = 0; }
	
	// ...
	
	int getVal() { return val; }
}

public class ConstructorRefDemo {
	
	public static void main(String args[]) {
		
		//引用的构造函数式 MyClass(int v)
		MyFunc myClassCons = MyClass::new;
		
		//实质上，myClassCons成了调用MyClass(int v)的另一种方式
		MyClass mc = myClassCons.func(100);
		
		System.out.println("val in mc is " + mc.getVal());
	}
}