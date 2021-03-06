package com.ruyicai.activity.usercenter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.palmdream.RuyicaiAndroid91.R;
import com.ruyicai.constant.Constants;
import com.ruyicai.util.PublicMethod;

public class ContentListView {
	Context context;
	public ContentListView(Context context){
		this.context = context;
	}
	/**
	 * 将竞彩的方案内容显示成列表形式
	 * @param json
	 */
	public void createListContent(LinearLayout layoutMain,TextView content,String lotno,String betcodehtml,JSONObject json){
		if(lotno.equals(Constants.LOTNO_JCLQ)||lotno.equals(Constants.LOTNO_JCLQ_DXF)||lotno.equals(Constants.LOTNO_JCLQ_RF)||
				lotno.equals(Constants.LOTNO_JCLQ_SFC)||lotno.equals(Constants.LOTNO_JCZQ)||lotno.equals(Constants.LOTNO_JCZQ_BF)||
				lotno.equals(Constants.LOTNO_JCZQ_BQC)||lotno.equals(Constants.LOTNO_JCZQ_ZQJ)){
			content.setVisibility(View.GONE);
			addContentView(layoutMain,json,lotno);
		}else if(lotno.equals(Constants.LOTNO_RX9)||lotno.equals(Constants.LOTNO_SFC)){
			addZQContentView(layoutMain,json,lotno);
		}else if(lotno.equals(Constants.LOTNO_JQC)||lotno.equals(Constants.LOTNO_LCB)){
			addJQCContentView(layoutMain,json,lotno);
		}else{
			content.setText(Html.fromHtml("方案内容：<br>"+betcodehtml));
		}
	}
	public void addZQContentView(LinearLayout layoutMain,JSONObject json,String lotno){
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewTop = inflate.inflate(R.layout.bet_query_zc_info, null);
		
		try {
			String disPlay = json.getString("display");
			if(disPlay.equals("true")){
				JSONArray jsonArray = json.getJSONArray("result");
				for(int i=0;i<jsonArray.length();i++){
					JSONObject obj = jsonArray.getJSONObject(i);
					TextView text = new TextView(context);
					text.setTextColor(context.getResources().getColor(R.color.black));
					text.setText("玩法："+obj.getString("play"));
					layoutMain.addView(text);
					layoutMain.addView(viewTop);
					JSONArray objArray = obj.getJSONArray("result");
					for(int j=0;j<objArray.length();j++){
						JSONObject itemJson = objArray.getJSONObject(j);
						View viewItem = inflate.inflate(R.layout.bet_query_zc_info_item, null);
						TextView textNum = (TextView) viewItem.findViewById(R.id.bet_query_text_num);
						TextView textTeam = (TextView) viewItem.findViewById(R.id.bet_query_text_team);
						TextView textScore = (TextView) viewItem.findViewById(R.id.bet_query_text_score);
						TextView textover = (TextView) viewItem.findViewById(R.id.bet_query_text_over);
						TextView textCheck = (TextView) viewItem.findViewById(R.id.bet_query_text_check);
						textNum.setText(itemJson.getString("teamId"));
						String teamVs = "vs";
						String homeTeam = itemJson.getString("homeTeam");
						String guestTeam = itemJson.getString("guestTeam");
						String isDanMa = "";
						try{
							isDanMa = itemJson.getString("isDanMa");
						}catch (Exception e) {
							// TODO: handle exception
						}
						
						String teamStr = guestTeam+" "+teamVs+" "+homeTeam;
						textTeam.setText(teamStr);
						
						if(isDanMa.equals("true")){
							SpannableStringBuilder builder = new SpannableStringBuilder();
							builder.append(Html.fromHtml(itemJson.getString("betContent"))+"(胆)");
							builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.jc_join_dan)), builder.length()-3, builder.length(), Spanned.SPAN_COMPOSING);
							textCheck.setText(builder, BufferType.EDITABLE);
						}else{
							textCheck.setText(Html.fromHtml(itemJson.getString("betContent")));
						}
						layoutMain.addView(viewItem);
					}
				}
				layoutMain.addView(viewTop);
			}else{
				String visiable = json.getString("visibility");
				TextView text = new TextView(context);
				text.setTextColor(context.getResources().getColor(R.color.black));
				text.setText(getState(visiable));
				layoutMain.addView(text);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void addJQCContentView(LinearLayout layoutMain,JSONObject json,String lotno){
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewTop = inflate.inflate(R.layout.bet_query_jqc_info, null);
		TextView textTopOver = (TextView) viewTop.findViewById(R.id.bet_query_text_over);
		TextView textTopCheck = (TextView) viewTop.findViewById(R.id.bet_query_text_check);
		try {
			String disPlay = json.getString("display");
			if(disPlay.equals("true")){
				JSONArray jsonArray = json.getJSONArray("result");
				for(int i=0;i<jsonArray.length();i++){
					JSONObject obj = jsonArray.getJSONObject(i);
					TextView text = new TextView(context);
					text.setTextColor(context.getResources().getColor(R.color.black));
					text.setText("玩法："+obj.getString("play"));
					layoutMain.addView(text);
					layoutMain.addView(viewTop);
					JSONArray objArray = obj.getJSONArray("result");
					for(int j=0;j<objArray.length();j++){
						JSONObject itemJson = objArray.getJSONObject(j);
						View viewItem = inflate.inflate(R.layout.bet_query_jqc_info_item, null);
						TextView textNum = (TextView) viewItem.findViewById(R.id.bet_query_text_num);
						TextView textTeam = (TextView) viewItem.findViewById(R.id.bet_query_text_team);
						TextView textScore = (TextView) viewItem.findViewById(R.id.bet_query_text_score);
						TextView textCheck = (TextView) viewItem.findViewById(R.id.bet_query_text_check);
						textNum.setText(itemJson.getString("teamId"));
						String teamVs = "vs";
						
						String homeTeam = itemJson.getString("homeTeam");
						String guestTeam = itemJson.getString("guestTeam");
						
						String teamStr = guestTeam+" "+teamVs+" "+homeTeam;
						textTeam.setText(teamStr);
						
						if(lotno.equals(Constants.LOTNO_JQC)){
							textScore.setText(Html.fromHtml(itemJson.getString("betContentHome")));
							textCheck.setText(Html.fromHtml(itemJson.getString("betContentGuest")));
						}else{
							textTopOver.setText("半场");
							textTopCheck.setText("全场");
							textScore.setText(Html.fromHtml(itemJson.getString("betContentHalf")));
							textCheck.setText(Html.fromHtml(itemJson.getString("betContentAll")));
						}
						
						layoutMain.addView(viewItem);
					}
				}
				layoutMain.addView(viewTop);
			}else{
				String visiable = json.getString("visibility");
				TextView text = new TextView(context);
				text.setTextColor(context.getResources().getColor(R.color.black));
				text.setText(getState(visiable));
				layoutMain.addView(text);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void addContentView(LinearLayout layoutMain,JSONObject json,String lotno){
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewTop = inflate.inflate(R.layout.bet_query_jc_info, null);
		
		try {
			String disPlay = json.getString("display");
			if(disPlay.equals("true")){
				JSONArray jsonArray = json.getJSONArray("result");
				for(int i=0;i<jsonArray.length();i++){
					JSONObject obj = jsonArray.getJSONObject(i);
					View viewItem = inflate.inflate(R.layout.bet_query_jc_info_item, null);
					TextView textNum = (TextView) viewItem.findViewById(R.id.bet_query_text_num);
					TextView textTeam = (TextView) viewItem.findViewById(R.id.bet_query_text_team);
					TextView textScore = (TextView) viewItem.findViewById(R.id.bet_query_text_score);
					TextView textover = (TextView) viewItem.findViewById(R.id.bet_query_text_over);
					TextView textCheck = (TextView) viewItem.findViewById(R.id.bet_query_text_check);
					textNum.setText(getWeek(obj.getString("weekId"))+obj.getString("teamId"));
					String letScore = obj.getString("letScore");
					String totalScore = obj.getString("totalScore");
					String teamVs = "vs";
					if(!letScore.equals("")){
						teamVs = letScore;
					}else if(!totalScore.equals("")){
						teamVs = totalScore;
					}
					String guestScore = obj.getString("guestScore");
					String homeScore = obj.getString("homeScore");
					String homeTeam = obj.getString("homeTeam");
					String guestTeam = obj.getString("guestTeam");
					String isDanMa = obj.getString("isDanMa");
					
					try{
						Integer guestInt = Integer.parseInt(guestScore);
						Integer hometInt = Integer.parseInt(homeScore);
						if(guestInt>hometInt){
							textover.setText("主负");
						}else if(guestInt<hometInt){
							textover.setText("主胜");
						}else if(guestInt==hometInt){
							textover.setText("平");
						}
					}catch(Exception e){
						
					}
					if(lotno.equals(Constants.LOTNO_JCLQ)||lotno.equals(Constants.LOTNO_JCLQ_DXF)||lotno.equals(Constants.LOTNO_JCLQ_RF)||lotno.equals(Constants.LOTNO_JCLQ_SFC)){
						String teamStr = guestTeam+"\n"+teamVs+"\n"+homeTeam;
						if(!letScore.equals("")){
							PublicMethod.setTextColor(textTeam,guestTeam.length(), guestTeam.length() + teamVs.length()+1, teamStr,Color.BLUE);
						}else{
							textTeam.setText(teamStr);
						}
						if(!guestScore.equals("")&&!homeScore.equals("")){
							textScore.setText(guestScore+":"+homeScore);
						}
				
					}else{
						String teamStr = homeTeam+"\n"+teamVs+"\n"+guestTeam;
						if(!letScore.equals("")){
							PublicMethod.setTextColor(textTeam,homeTeam.length(), homeTeam.length() + teamVs.length()+1, teamStr,Color.BLUE);
						}else{
							textTeam.setText(teamStr);
						}
						
						if(!guestScore.equals("")&&!homeScore.equals("")){
							textScore.setText(homeScore+":"+guestScore);
						}
					}
					if(isDanMa.equals("true")){
						SpannableStringBuilder builder = new SpannableStringBuilder();
						builder.append(Html.fromHtml(obj.getString("betContentHtml"))+"(胆)");
						builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.jc_join_dan)), builder.length()-3, builder.length(), Spanned.SPAN_COMPOSING);
						textCheck.setText(builder, BufferType.EDITABLE);
					}else{
						textCheck.setText(Html.fromHtml(obj.getString("betContentHtml")));
					}
					if(i==0){
						TextView text = new TextView(context);
						text.setTextColor(context.getResources().getColor(R.color.black));
						text.setText("玩法："+obj.getString("play"));
						layoutMain.addView(text);
						layoutMain.addView(viewTop);
					}
					layoutMain.addView(viewItem);
				}
			}else{
				String visiable = json.getString("visibility");
				TextView text = new TextView(context);
				text.setTextColor(context.getResources().getColor(R.color.black));
				text.setText(getState(visiable));
				layoutMain.addView(text);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public String getState(String visibility){
		String returnStr = "";
		if(visibility.equals("")|visibility.equals("1")){
	        returnStr = "保密";
		}else if(visibility.equals("2")){
			returnStr = "对所有人立即公开";
		}else if(visibility.equals("3")){
			returnStr = "对跟单者立即公开";
		}else if(visibility.equals("4")){
			returnStr = "对跟单者截止后公开";
		}
		return returnStr;
	}
	public void setDisplayText(){
		
	}
	public String getWeek(String weekId){
		String week = "";
		if(weekId.equals("1")){
			week = "周一";
		}else if(weekId.equals("2")){
			week = "周二";
		}else if(weekId.equals("3")){
			week = "周三";
		}else if(weekId.equals("4")){
			week = "周四";
		}else if(weekId.equals("5")){
			week = "周五";
		}else if(weekId.equals("6")){
			week = "周六";
		}else if(weekId.equals("7")){
			week = "周七";
		}
		return week;
	}
}
