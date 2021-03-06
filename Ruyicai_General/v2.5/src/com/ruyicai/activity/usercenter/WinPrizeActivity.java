/**
 * 
 */
package com.ruyicai.activity.usercenter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.handler.HandlerMsg;
import com.ruyicai.handler.MyHandler;
import com.ruyicai.net.newtransaction.BetAndGiftInterface;
import com.ruyicai.net.newtransaction.WinQueryInterface;
import com.ruyicai.net.newtransaction.pojo.BetAndGiftPojo;
import com.ruyicai.net.newtransaction.pojo.BetAndWinAndTrackAndGiftQueryPojo;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.ShellRWSharesPreferences;
/**
 * 中奖查询联网
 * @author Administrator
 *
 */
public class WinPrizeActivity extends Activity {
	private BetAndGiftPojo betPojo = new BetAndGiftPojo();
	private LinearLayout usecenerLinear;
	private Button returnButton;
	private TextView	titleTextView;
	String jsonString;
	ListView queryinfolist;
	final	String  BATCHCODE="batchCode",BETCODE="betCode",CASHTIME="cashTime",LOTNAME = "lotName",
	             LOTNO="lotNo",WINAMOUNT="winAmount",SELLTIME = "sellTime";
	private final int DIALOG1_KEY = 0;
	private String phonenum,sessionid,userno;
	List<Vector> windatalist = new ArrayList<Vector>(); 
	Context context = this ;
	ProgressDialog dialog;
	String jsonobject;
	int  allPage;
	int pageindex;
	boolean isfirst = false;
	TextView pagetext;
	 Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				dialog.dismiss();
				Toast.makeText(WinPrizeActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			case 1:
				dialog.dismiss();
				encodejson((String) msg.obj);
				List list = initAdapterlist(pageindex);
        		initListView(queryinfolist,list);
				break;
			}
		}
	 };
	public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.usercenter_mainlayout);
			
			returnButton = (Button)findViewById(R.id.layout_usercenter_img_return);
			titleTextView = (TextView)findViewById(R.id.usercenter_mainlayou_text_title);
			returnButton.setBackgroundResource(R.drawable.returnselecter);
			titleTextView.setText(R.string.usercenter_winningCheck);
			returnButton.setText(R.string.returnlastpage);
			returnButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
			jsonobject = this.getIntent().getStringExtra("winjson");
			encodejson(jsonobject);
			isfirst = true;
			initLinear();
	}
	protected  void initReturn(){
		returnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					finish();
			}
		});
	}
	 private void initPojo(){
			ShellRWSharesPreferences shellRW = new ShellRWSharesPreferences(this, "addInfo");
			phonenum = shellRW.getUserLoginInfo("phonenum");
			sessionid = shellRW.getUserLoginInfo("sessionid");
			userno = shellRW.getUserLoginInfo("userno");
			
	}
	private void	getWinDataNet(final int pageindex){
		showDialog(0);
		new Thread(new Runnable() {
			public void run() {
			initPojo();
			BetAndWinAndTrackAndGiftQueryPojo winQueryPojo = new BetAndWinAndTrackAndGiftQueryPojo();
			winQueryPojo.setUserno(userno);
			winQueryPojo.setSessionid(sessionid);
			winQueryPojo.setPhonenum(phonenum);
			winQueryPojo.setPageindex(String.valueOf(pageindex));
			winQueryPojo.setMaxresult("10");
			winQueryPojo.setType("win");
			
				Message msg = new Message();
				jsonString = WinQueryInterface.getInstance().winQuery(winQueryPojo);
				try {
					JSONObject aa = new JSONObject(jsonString);
					String errcode = aa.getString("error_code");
					String message = aa.getString("message");
					if(errcode.equals("0000")){
						msg.what = 1;
						msg.obj = jsonString;
						handler.sendMessage(msg);
					}else{
						msg.what = 0;
						msg.obj = message;
						handler.sendMessage(msg);					
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}
	private void initLinear(){
		usecenerLinear = (LinearLayout)findViewById(R.id.usercenterContent);
		usecenerLinear.addView(initLinearView());
	}
	private  View	initLinearView(){
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = (LinearLayout) inflate.inflate(R.layout.usercenter_listview_layout, null);
		queryinfolist = (ListView) view.findViewById(R.id.usercenter_listview_queryinfo);
		pagetext = (TextView)view.findViewById(R.id.usercenter_text_pagetext);
		ImageButton subpagebtn = (ImageButton)view.findViewById(R.id.usercenter_btn_subpage);
		subpagebtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				isfirst = false;
				 pageindex--;
	               if(pageindex<0){
	            	   pageindex = 0;
	   				Toast.makeText(WinPrizeActivity.this,R.string.usercenter_hasgonefirst, Toast.LENGTH_SHORT).show();   
	               }else{
	            	   	List list = initAdapterlist(pageindex);
		        		initListView(queryinfolist,list);
	               }
	             
//	       		pagetext.setText(initPageTextView(pageindex));
			}
		});
		ImageButton addpagebtn = (ImageButton)view.findViewById(R.id.usercenter_btn_addpage);
		addpagebtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			   isfirst = false;
			   if(pageindex >windatalist.size()-1){
				   pageindex = windatalist.size()-1;
			   }
               pageindex++;
	           if(pageindex<allPage){
		        	if(pageindex>windatalist.size()-1){
		        		getWinDataNet(pageindex);
		        		
		        	}else{
		        		List list = initAdapterlist(pageindex);
		        		initListView(queryinfolist,list);
		        	}
			   }else{
				   pageindex=allPage-1;
				Toast.makeText(WinPrizeActivity.this,R.string.usercenter_hasgonelast, Toast.LENGTH_SHORT).show();   
			   }
	        
			}
		});
		
		if(isfirst){
			List list = initAdapterlist(pageindex);
			initListView(queryinfolist,list);
		}
		return view;
	}
	private String initPageTextView(int pageindex){
		StringBuffer str = new StringBuffer();
		String zi_gong = getString(R.string.usercenter_gong);
		String zi_ye = getString(R.string.usercenter_page);
		String zi_di = getString(R.string.usercenter_di);
		str.append(zi_di).append(pageindex+1+"").append(zi_ye).append(zi_gong).append(String.valueOf(allPage)).append(zi_ye);
		return str.toString();
	}
	private void initListView(ListView listview,List list){
		pagetext.setText(initPageTextView(pageindex));
		WinPrizeAdapter adapter = new WinPrizeAdapter(context,list);
		Log.v("1111111111","-----");
		listview.setAdapter(adapter);
		
	}
	public AlertDialog lookDetailDialog(String detail){
		LayoutInflater factory = LayoutInflater.from(this);
		/*中奖查询的查看详情使用余额查询的布局*/
		View	view = factory.inflate(R.layout.usercenter_balancequery, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		TextView  title = (TextView)view.findViewById(R.id.usercenter_balancequery_title);
		title.setText(R.string.usercenter_detailsTitle);
		TextView	detailTextView = (TextView)view.findViewById(R.id.balanceinfo);
		detailTextView.setText(Html.fromHtml(detail));
		Button cancleLook = (Button)view.findViewById(R.id.usercenter_balancequery_back);
		cancleLook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.show();
		dialog.getWindow().setContentView(view);
		return dialog;
			
	}
	  public List<Map<String, Object>> initAdapterlist(int pageindex){
		  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(2);
			for (int i = 0; i < windatalist.get(pageindex).size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(BATCHCODE, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getBatchCode());
				map.put(BETCODE, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getBetCode());
				map.put(LOTNO, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getLotNo());
				map.put(WINAMOUNT, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getWinAmount());
				map.put(CASHTIME, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getCashTime());
				map.put(SELLTIME, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getSellTime());
				map.put(LOTNAME, ((WinPrizeQueryInfo) windatalist.get(pageindex).get(i)).getLotName());
				list.add(map);
			}
			return list;
	  }  
	
	  public void encodejson(String json) {
		  
		  try {
			  JSONObject  winprizejsonobj = new JSONObject(json);
			  allPage = Integer.parseInt(winprizejsonobj.getString("totalPage"));
			  String maxpage = "";
			  String winprizejsonstring = winprizejsonobj.getString("result");
				
			  JSONArray winprizejson = new JSONArray(winprizejsonstring);
			  Log.v("1111111111111111",""+winprizejson);
			  Vector<WinPrizeQueryInfo> winInfos = new Vector<WinPrizeQueryInfo>(); 
			  for(int i=0;i<winprizejson.length();i++){
				  try{
						WinPrizeQueryInfo winPrizeQueryinfo = new WinPrizeQueryInfo();
						winPrizeQueryinfo.setBatchCode(winprizejson.getJSONObject(i).getString(BATCHCODE));
						winPrizeQueryinfo.setBetCode(winprizejson.getJSONObject(i).getString(BETCODE));
						winPrizeQueryinfo.setCashTime(winprizejson.getJSONObject(i).getString(CASHTIME));
						winPrizeQueryinfo.setLotNo(winprizejson.getJSONObject(i).getString(LOTNO));
						winPrizeQueryinfo.setLotName(winprizejson.getJSONObject(i).getString(LOTNAME));
						winPrizeQueryinfo.setWinAmount(winprizejson.getJSONObject(i).getString(WINAMOUNT));
						winPrizeQueryinfo.setSellTime(winprizejson.getJSONObject(i).getString(SELLTIME));
						winInfos.add(winPrizeQueryinfo);
				  }catch (Exception e) {
						Log.v("22222222222","解析中奖查询json串错误");
						Log.v("22222222222","json串："+winprizejson);
				  }
			  }
			  windatalist.add(winInfos);
			  } catch (JSONException e) {
			  		try {
						JSONObject winprizejson = new JSONObject(json);
						Log.v("winprizejson",""+winprizejson);
			  		} catch (JSONException e1) {
		  				Log.v("NETBack",jsonobject);
			  		}
			  }
	  }
	class WinPrizeQueryInfo{
		private String winAmount;
		private String batchCode;
		private String lotNo;
		private String lotName;
		private String betCode;
		private String cashTime;
		private String sellTime;
		private String bet_code;
		public String getLotName() {
			return lotName;
		}
		public void setLotName(String lotName) {
			this.lotName = lotName;
		}
	
		public String getBet_code() {
			return bet_code;
		}
		public void setBet_code(String betCode) {
			bet_code = betCode;
		}
		public String getSellTime() {
			return sellTime;
		}
		public void setSellTime(String sellTime) {
			this.sellTime = sellTime;
		}
		public String getWinAmount() {
			return winAmount;
		}
		public void setWinAmount(String winAmount) {
			this.winAmount = winAmount;
		}
		public String getBatchCode() {
			return batchCode;
		}
		public void setBatchCode(String batchCode) {
			this.batchCode = batchCode;
		}
		public String getLotNo() {
			return lotNo;
		}
		public void setLotNo(String lotNo) {
			this.lotNo = lotNo;
		}
		public String getBetCode() {
			return betCode;
		}
		public void setBetCode(String betCode) {
			this.betCode = betCode;
		}
		public String getCashTime() {
			return cashTime;
		}
		public void setCashTime(String cashTime) {
			this.cashTime = cashTime;
		}
	}
	/**
	 * 中奖查询的适配器
	 */
	public class WinPrizeAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater; // 扩充主列表布局
			private List<Map<String, Object>> mList;
			public WinPrizeAdapter(Context context, List<Map<String, Object>> list) {
				Log.v("WinPrizeAdapter","3333333");
				mInflater = LayoutInflater.from(context);
				mList = list;
			}
			public int getCount() {
				return mList.size();
			}
			public Object getItem(int position) {
				return mList.get(position);
			}
			public long getItemId(int position) {
				return position;
			}
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				final String lotName = (String) mList.get(position).get(LOTNAME);
				final 	String prizeqihao = (String) mList.get(position).get(BATCHCODE);
				String winAmount = (String) mList.get(position).get(WINAMOUNT);
				final String prizemoney = PublicMethod.formatMoney(winAmount);
				final String cashtime = (String) mList.get(position).get(CASHTIME);
				final String sellTime = (String) mList.get(position).get(SELLTIME);
				final String betcode = (String) mList.get(position).get(BETCODE);
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.usercenter_listitem_winprize_query,null);
					holder = new ViewHolder();
					holder.lotteryname = (TextView) convertView.findViewById(R.id.usercenter_winprize_lotteryname);
					holder.prizeqihao = (TextView) convertView.findViewById(R.id.usercenter_winprize_prizeqihao);
					holder.prizemoney = (TextView) convertView.findViewById(R.id.usercenter_winprize_paymoney);
					holder.paytime = (TextView) convertView.findViewById(R.id.usercenter_winprize_prizemoney);
					holder.lookdetail = (Button)convertView.findViewById(R.id.usercenter_winprize_querydetail);
					holder.buyagain = (Button)convertView.findViewById(R.id.usercenter_winprize_buyagain);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				String prizeString = getString(R.string.usercenter_prizeMoney);//中奖金额字
				holder.lotteryname.setText(lotName);
				holder.prizeqihao.setText("(期号:"+prizeqihao+")");
				holder.paytime .setText(sellTime);
				holder.prizemoney.setText(Html.fromHtml(prizeString+"<font color=\"red\">"+prizemoney+"</font>"));
				holder.buyagain.setVisibility(Button.GONE);
				holder.lookdetail.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						StringBuffer detailinfo = new StringBuffer();
						detailinfo.append(getString(R.string.usercenter_lotterytypename)).append(lotName).append("<br>")
							.append(getString(R.string.usercenter_lotteryIssue)).append(prizeqihao).append("<br>")
							.append(getString(R.string.usercenter_bettingTime)).append(sellTime).append("<br>")
							.append(getString(R.string.usercenter_endTime)).append(cashtime).append("<br>")
							.append(getString(R.string.usercenter_prizeMoney)).append("<font color=\"red\">"+prizemoney+"</font>").append("<br>")
							.append(getString(R.string.usercenter_betcode)).append("<br>")
							.append(betcode);
						lookDetailDialog(""+detailinfo);
					}
				});
				return convertView;
			}
			class ViewHolder {
				TextView lotteryname;
				TextView prizeqihao;
				TextView paytime;//购彩时间
				TextView prizemoney;
				Button   lookdetail;
				Button  buyagain;
			}
	}
	protected Dialog onCreateDialog(int id) {
		 dialog = new ProgressDialog(this);
       switch (id) {
           case DIALOG1_KEY: {
               dialog.setTitle(R.string.usercenter_netDialogTitle);
               dialog.setMessage(getString(R.string.usercenter_netDialogRemind));
               dialog.setIndeterminate(true);
               dialog.setCancelable(true);
               return dialog;
           }
       }
       return dialog;
   }
}
