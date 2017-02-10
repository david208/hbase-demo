package com.sm.demo;

public class Ld {
	
	public static int ldExec(String a,String b){
		int aLen = a.length();
		int bLen = b.length();
		char[] aArray = a.toCharArray();
		char[] bArray = b.toCharArray();
		int max= Math.max(aLen,bLen);
		newAArray = new char[max];
		newBArray = new char[max];
		return run(aArray, bArray, aLen, bLen);
		
	}
	
	private static char[] newAArray,newBArray;
	
	
	public static int run(char[] aArray,char[] bArray,int i,int j){
		if (i==0){
			return j;
		}
		if (j==0){
			return i;
		}
		if (aArray[i-1] == bArray[j-1]){
			int max= Math.max(i-1, j-1);
			newAArray[max]=aArray[i-1];
			newBArray[max]=bArray[j-1];
			return run(aArray, bArray, i-1, j-1);
		}
		else{
			int ld1 = run(aArray, bArray, i-1, j-1);
			int ld2 = run(aArray, bArray, i-1, j);
			int ld3 = run(aArray, bArray, i, j-1);
			int min = Math.min(Math.min(ld1, ld2),ld3);
			int max= Math.max(i-1, j-1);
			if (ld1==min){
				newAArray[max]=aArray[i-1];
				newBArray[max]=bArray[j-1];
			}
			else if (ld2==min){
				newAArray[max]=aArray[i-1];
				newBArray[max]='_';
			}
			else if (ld3==min){
				newAArray[max]='_';
				newBArray[max]=bArray[j-1];
			}
			return min+1;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(ldExec("GGATCGA", "GAATTCAGTTA"));
		
		System.out.println(new String(newAArray)+":"+new String(newBArray));
	}

}
