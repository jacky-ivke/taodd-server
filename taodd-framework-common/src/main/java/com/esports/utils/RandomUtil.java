package com.esports.utils;

import java.util.*;

public class RandomUtil {

	public static String getUUID(String prefix) {
		synchronized (RandomUtil.class) {
			int machineId = 1;
			int hashCodeV = UUID.randomUUID().toString().hashCode();
			if (hashCodeV < 0) {
				hashCodeV = -hashCodeV;
			}
			return prefix + machineId + String.format("%015d", hashCodeV);
		}
	}

	public static String getRandomString(int length) {
		String str = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			// int number = random.nextInt(62);// [0,62)
			sb.append(str.charAt(random.nextInt(10)));
		}
		return sb.toString();
	}

	/**
	 * 随机指定范围内N个不重复的数 最简单最基本的方法
	 * 
	 * @param min 指定范围最小值
	 * @param max 指定范围最大值
	 * @param n   随机数个数
	 */
	public static int[] randomCommon(int min, int max, int n) {
		if (n > (max - min + 1) || max < min) {
			return null;
		}
		int[] result = new int[n];
		int count = 0;
		while (count < n) {
			int num = (int) (Math.random() * (max - min)) + min;
			boolean flag = true;
			for (int j = 0; j < n; j++) {
				if (num == result[j]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				result[count] = num;
				count++;
			}
		}
		return result;
	}

	public static int randomCommon() {
		int num = 0;
		int[] nums = RandomUtil.randomCommon(0, 99, 1);
		if (nums != null) {
			num = nums[0];
		}
		return num;
	}

	public static String randomName(int min,int max) {
		String name;
		char[] nameChar;
		//名字最长为max个,最短为min个
		int nameLength=(int)(Math.random()*(max-min+1))+min;	
		nameChar=new char[nameLength];
		//生成大写首字母
		nameChar[0]=(char) (Math.random()*26+65);				
		for(int i=1;i<nameLength;i++) {
			nameChar[i]=(char)(Math.random()*26+97);
		}
		name=new String(nameChar);
		return name;
	}
	
	 public static String randomPassword(int length) {
	     char[] chars = new char[]{
	            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	            '_', '*', '@', '%', '#', '^', '.', ',', '!', '$','^','-'
	    };
        List<String> list = new ArrayList<String>(chars.length);
        for (int i = 0; i < chars.length; i++) {
            list.add(String.valueOf(chars[i]));
        }
        Collections.shuffle(list);

        int count = 0;
        StringBuffer sb = new StringBuffer();
        Random random = new Random(System.nanoTime());
        while (count < length) {
            int i = random.nextInt(list.size());
            sb.append(list.get(i));
            count++;
        }
        return sb.toString();
    }
	
	public static void main(String[] args) {
	    for(int i=0;i<=8;i++) {
	        System.out.println(RandomUtil.randomPassword(6));
	    }
	    
	}
}
