package com.palmdream.RuyicaiAndroid;

import android.content.Context;
import android.content.SharedPreferences;

//类RWSharedPreferences的作用是用键值对的形式将用户输入的信息保存下来，并可以在下次使用时自动显示出来
public class RWSharedPreferences {
	SharedPreferences sp; // 定义一个SharedPreferences的对象
	SharedPreferences.Editor editor; // 通过 SharedPreferences.Editor的对象来修改数据

	Context context;

	/**
	 * 初始化信息
	 * 
	 * @param c
	 * @param name
	 *            名称
	 */
	public RWSharedPreferences(Context c, String name) {
		context = c;
		sp = context.getSharedPreferences(name, 0);
		editor = sp.edit();
	}

	/**
	 * 把value的值与key关联起来
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void putPreferencesValue(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 通过key可以得到对应的value值，返回value的值
	 * 
	 * @param key
	 *            键
	 * @return 返回key对应的值
	 */
	public String getPreferencesValue(String key) {
		return sp.getString(key, null);
	}
}
