package javaDemo;

interface MyFunc<T> {
	int func(T[] vals, T v);
}

class MyArrayOps {
	//泛型方法
	static <T> int countMatching(T[] vals, T v) {
		int count = 0;
		
		for(int i=0; i<vals.length; i++){
			if(vals[i] == v) count++;
		}
		
		return count;
	}
}

public class GenericMethodRefDemo {
	
	static <T> int myOp(MyFunc<T> f, T[] vals, T v) {
		return f.func(vals, v);
	}
	
	public static void main(String args[]){
		
		Integer[] vals = { 1, 2, 3, 4, 2, 3, 4, 4, 5};
		String[] strs = { "One", "Two", "Three", "Two"};
		int count;
		
		//注意这里如何指定泛型类型参数
		count = myOp(MyArrayOps::<Integer>countMatching, vals, 4);
		System.out.println("vals contains " + count + " 4s");
		
		count = myOp(MyArrayOps::<String>countMatching, strs, "Two");
		System.out.println("strs contains " + count + " Twos");
	}
}
