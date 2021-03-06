/**
 * 
 */
package com.ruyicai.activity.buy.jixuan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

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
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.palmdream.RuyicaiAndroidxuancai.R;
import com.ruyicai.activity.buy.ApplicationAddview;
import com.ruyicai.activity.buy.zixuan.AddView;
import com.ruyicai.activity.buy.zixuan.OrderDetails;
import com.ruyicai.activity.buy.zixuan.AddView.CodeInfo;
import com.ruyicai.activity.buy.zixuan.ZixuanActivity;
import com.ruyicai.activity.common.UserLogin;
import com.ruyicai.activity.gift.GiftActivity;
import com.ruyicai.activity.join.JoinStartActivity;
import com.ruyicai.constant.Constants;
import com.ruyicai.handler.HandlerMsg;
import com.ruyicai.handler.MyHandler;
import com.ruyicai.interfaces.BuyImplement;
import com.ruyicai.jixuan.Balls;
import com.ruyicai.net.newtransaction.BetAndGiftInterface;
import com.ruyicai.net.newtransaction.pojo.BetAndGiftPojo;
import com.ruyicai.pojo.AreaNum;
import com.ruyicai.util.PublicMethod;
import com.ruyicai.util.SensorActivity;
import com.ruyicai.util.RWSharedPreferences;
import com.umeng.analytics.MobclickAgent;

/**
 * 单式机选父类
 * @author Administrator
 *
 */
public class DanshiJiXuan extends Activity implements SeekBar.OnSeekBarChangeListener,HandlerMsg{
	private TextView mTextSumMoney;
	public SeekBar mSeekBarBeishu, mSeekBarQishu;
	private TextView mTextBeishu, mTextQishu;
	public int iProgressBeishu = 1, iProgressQishu = 1;
	private Spinner jixuanZhu;
	private LinearLayout zhumaView;
	private SsqSensor sensor = new SsqSensor(this);
	private boolean isOnclik = true;
	public Vector<Balls> balls = new Vector();
	private int redBallResId[] = { R.drawable.grey, R.drawable.red };// 选区小球切换图片
	private int blueBallResId[] = { R.drawable.grey, R.drawable.blue };// 选区小球切换图片
	private Balls ballOne;
	private BuyImplement buyImplement;//投注要实现的方法
	private Toast toast;
	private boolean toLogin = false;
	ProgressDialog progressdialog;
	private static final int DIALOG1_KEY = 0;// 进度条的值2010/7/4
	public BetAndGiftPojo betAndGift=new BetAndGiftPojo();//投注信息类
	MyHandler handler = new MyHandler(this);//自定义handler
	public String phonenum,sessionId,userno;
	public boolean isGift = false;//是否赠送
	public boolean isJoin = false;//是否合买
	public boolean isTouzhu = false;//是否投注
	private int oneValue = 2;//单注金额
	String codeStr;
	String lotno;
	public int iZhuShu;
	RadioButton  check;
	RadioButton  joinCheck;
	RadioButton  touzhuCheck;
	TextView alertText;
	TextView issueText;
	AlertDialog touZhuDialog;
	TextView textAlert;
	TextView textZhuma;
    TextView textTitle;
    Button codeInfo;
	boolean isTen = true;
	public AddView addView;
	private final int All_ZHU = 99;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buy_danshi_jixuan_activity_new);
	}
	/**
	 * 创建机选界面
	 */
	public void createView(Balls balles,BuyImplement buyImplement,boolean isTen){
		this.isTen = isTen;
		sensor.startAction();
		this.buyImplement = buyImplement;
		this.ballOne = balles;
		zhumaView = (LinearLayout) findViewById(R.id.buy_danshi_jixuan_linear_zhuma);
		zhumaView.removeAllViews();
		toast = Toast.makeText(this, "左右摇晃手机，重新选号！", Toast.LENGTH_SHORT);
		toast.show();
		balls = new Vector<Balls>();
		LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// 初始化spinner
			jixuanZhu = (Spinner) findViewById(R.id.buy_danshi_jixuan_spinner);
			if(Constants.SCREEN_WIDTH==240){
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(PublicMethod.getPxInt(90,DanshiJiXuan.this),
						LinearLayout.LayoutParams.WRAP_CONTENT);
				jixuanZhu.setLayoutParams(param);
			}
			jixuanZhu.setSelection(4);
			jixuanZhu.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					int position = jixuanZhu.getSelectedItemPosition();
					if (isOnclik) {
						again();
					} else {
						isOnclik = true;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		int index = jixuanZhu.getSelectedItemPosition() + 1;
		for (int i = 0; i < index; i++) {
			Balls ball = ballOne.createBalls();
			balls.add(ball);
		}
		createTable(zhumaView);
		sensor.onVibrator();// 震动
		Button zixuanTouzhu = (Button) findViewById(R.id.buy_danshi_jixuan_img_touzhu);
		zixuanTouzhu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				beginTouZhu();
			}
		});
		final TextView textNum = (TextView)findViewById(R.id.buy_zixuan_add_text_num);
		Button add_dialog = (Button) findViewById(R.id.buy_zixuan_img_add_delet);
		addView = new AddView(textNum,this,false);
		add_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(addView.getSize()>0){
					showAddViewDialog();
				}else{
					 Toast.makeText(DanshiJiXuan.this, DanshiJiXuan.this.getString(R.string.buy_add_dialog_alert), Toast.LENGTH_SHORT).show();	
				}
			}
		});
		Button add = (Button) findViewById(R.id.buy_zixuan_img_add);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
               addToCodes();
			}
		
		});

//		ImageButton again = (ImageButton) findViewById(R.id.buy_danshi_jixuan_img_again);
//		again.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				zhumaView.removeAllViews();
//				balls = new Vector();
//				for (int i = 0; i < jixuanZhu.getSelectedItemPosition() + 1; i++) {
//					Balls ball = ballOne.createBalls();
//					balls.add(ball);
//				}
//				createTable(zhumaView);
//			}
//		});
	}
	/**
	 * 将选取信息添加到号码篮里
	 */
	private void addToCodes() {
		if(addView.getSize()+balls.size()-1>=All_ZHU){
			Toast.makeText(DanshiJiXuan.this,DanshiJiXuan.this.getString(R.string.buy_add_view_zhu_alert) , Toast.LENGTH_SHORT).show();
		}else{
			getCodeInfo(addView);
			addView.updateTextNum();
			again();	
		}
		
	}
	
	public void getCodeInfo(AddView addView){
		for(int i=0;i<balls.size();i++){
			String codeStr = getAddZhuma(i);
			CodeInfo codeInfo = addView.initCodeInfo(betAndGift.getAmt(), 1);
			codeInfo.addAreaCode(codeStr,Color.BLACK);
			codeInfo.setTouZhuCode(balls.get(i).getZhuma(balls,1));
			addView.addCodeInfo(codeInfo);
		}   
		addView.setIsJXcode(ballOne.getZhuma(balls, iProgressBeishu));
	}
	/**
	 * 重新选择方法
	 */
	private void again() {
		zhumaView.removeAllViews();
		balls = new Vector();
		for (int i = 0; i < jixuanZhu.getSelectedItemPosition() + 1; i++) {
			Balls ball = ballOne.createBalls();
			balls.add(ball);
		}
		createTable(zhumaView);
	}
	private void showAddViewDialog() {
		addView.createDialog(DanshiJiXuan.this.getString(R.string.buy_add_dialog_title));
		addView.showDialog();
	}
	/**
	 * fqc edit 添加一个参数 isBeiShu 来判断当前是倍数还是期数 。
	 * 
	 * @param idFind
	 * @param iV
	 * @param isAdd
	 * @param mSeekBar
	 * @param isBeiShu
	 */
	private void setSeekWhenAddOrSub(int idFind, final int isAdd,final SeekBar mSeekBar, final boolean isBeiShu,View view) {
		ImageButton subtractBeishuBtn = (ImageButton) view.findViewById(idFind);
		subtractBeishuBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBeiShu) {
					if (isAdd == 1) {
						mSeekBar.setProgress(++iProgressBeishu);
					} else {
						mSeekBar.setProgress(--iProgressBeishu);
					}
					iProgressBeishu = mSeekBar.getProgress();
				} else {
					if (isAdd == 1) {
						mSeekBar.setProgress(++iProgressQishu);
					} else {
						mSeekBar.setProgress(--iProgressQishu);
					}
					iProgressQishu = mSeekBar.getProgress();
				}
			}
		});
	}
	/**
	 * 投注方法
	 */
	private void beginTouZhu() {
			if (balls.size() == 0) {
				alertInfo("请至少选择1注彩票");
			} else {
				if(addView.getSize()==0){
					addToCodes();
					alert_jixuan();
				}else{
					showAddViewDialog();
				}
				
			}
	}
    public void setOneValue(int value){
    	oneValue = value;
    }
	/**
	 * 直选机选投注提示框中的信息
	 */
	private String getTouzhuAlertJixuan() {
		int oneAmt = betAndGift.getAmt();
        int iZhushu = getZhushu();
		int beishu = mSeekBarBeishu.getProgress();
		return "注数："+ iZhushu + "注    " + "金额："
				+ oneAmt * (mSeekBarQishu.getProgress() * iZhushu*beishu) + "元" ;
	}
	private int getZhushu(){
		return addView.getSize();
	}
	/**
	 * 投注提示显示注码
	 * @return
	 */
	private String getZhuma(){
		String zhumaString = "";
		for (int i = 0; i < balls.size(); i++) {
			for(int j=0;j<balls.get(i).getVZhuma().size();j++){
				if(isTen){
					zhumaString += balls.get(i).getTenShowZhuma(j);
				}else{
					zhumaString += balls.get(i).getShowZhuma(j);
				}
				
				if(j!=balls.get(i).getVZhuma().size()-1){
					zhumaString += "+";
				}
			}
			if (i != balls.size() - 1) {
				zhumaString += "\n";
			}
		}
		return "注码：" + "\n" + zhumaString ;	
	}
	/**
	 * 购彩篮显示注码
	 * @return
	 */
	private String getAddZhuma(int index){
		String zhumaString = "";
			for(int j=0;j<balls.get(index).getVZhuma().size();j++){
				if(isTen){
					zhumaString += balls.get(index).getTenShowZhuma(j);
				}else{
					zhumaString += balls.get(index).getShowZhuma(j);
				}
				
				if(j!=balls.get(index).getVZhuma().size()-1){
					zhumaString += "|";
				}
			}
		return zhumaString ;	
	}
	/**
	 * seekBar组件的监听器
	 */
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		// TODO Auto-generated method stub
		if (progress < 1)
			seekBar.setProgress(1);
		int iProgress = seekBar.getProgress();
		switch (seekBar.getId()) {
		case R.id.buy_zixuan_seek_beishu:
			iProgressBeishu = iProgress;
			mTextBeishu.setText("" + iProgressBeishu);
			break;
		case R.id.buy_zixuan_seek_qishu:
			iProgressQishu = iProgress;
			mTextQishu.setText("" + iProgressQishu);
			stateCheck();
			break;
		default:
			break;
		}
		alertText.setText(getTouzhuAlertJixuan());

	}
	/**
	 * 创建直选机选
	 */
	public void createTable(LinearLayout layout) {
		for (int i = 0; i < balls.size(); i++) {
			final int index = i;
			int iScreenWidth = PublicMethod.getDisplayWidth(this);
			LinearLayout lines = new LinearLayout(layout.getContext());
			for(int j=0;j<balls.get(i).getVZhuma().size();j++){
				 String color = (String) balls.get(i).getVColor().get(j);
				 TableLayout table;
				if(color.equals("red")){
					table = PublicMethod.makeBallTableJiXuan(null,iScreenWidth,redBallResId, balls.get(i).getBalls(j), this);
				}else{
				    table = PublicMethod.makeBallTableJiXuan(null,iScreenWidth,blueBallResId, balls.get(i).getBalls(j), this);
				}
					
				lines.addView(table);
			}	
			ImageButton delet = new ImageButton(lines.getContext());
			delet.setBackgroundResource(R.drawable.shanchu);
			delet.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (balls.size() > 1 && index < balls.size()) {
						zhumaView.removeAllViews();
						balls.remove(index);
						isOnclik = false;
						jixuanZhu.setSelection(balls.size() - 1);
						createTable(zhumaView);
					} else {
						Toast.makeText(DanshiJiXuan.this, getResources().getText(R.string.zhixuan_jixuan_toast),Toast.LENGTH_SHORT).show();
					}
				}
			});	
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			param.setMargins(10, 5, 0, 0);
			lines.addView(delet, param);
			lines.setGravity(Gravity.CENTER_HORIZONTAL);
            if(i%2==0){
				lines.setBackgroundResource(R.drawable.jixuan_list_bg);
			}
            lines.setPadding(0, 3, 0, 0);
			layout.addView(lines);
			
		}

	}
	/**
	 * 提示框1 用来提醒选球规则
	 * @param string     显示框信息
	 * @return
	 */
	public void alertInfo(String string) {   
		Builder dialog = new AlertDialog.Builder(this).setTitle("请选择号码")
				.setMessage(string).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						});
		dialog.show();

	}

	/**
	 * 机选投注调用函数
	 * @param string         显示框信息
	 * @return
	 */
	public void alert_jixuan() {
//		sensor.stopAction();
		 buyImplement.touzhuNet();//投注类型和彩种
		 setZhuShu(balls.size());
		 toLogin = false;
//		 isGift = false;
//		 isJoin = false;
//		 isTouzhu = true; 
		 initBet();
		 ApplicationAddview app=(ApplicationAddview)getApplicationContext();
		 app.setPojo(betAndGift);
		 app.setAddview(addView);
	     Intent intent =new Intent(DanshiJiXuan.this,OrderDetails.class); 
		 startActivity(intent);
	}
	   /**
	    * 当前注数
	    */
		public void setZhuShu(int zhushu){
			iZhuShu = zhushu;
		}
	/**
	 * 初始化倍数和期数进度条
	 * @param view
	 */
    public void initImageView(View view){
		mSeekBarBeishu = (SeekBar) view.findViewById(R.id.buy_zixuan_seek_beishu);
		mSeekBarBeishu.setOnSeekBarChangeListener(this);
		mSeekBarBeishu.setProgress(iProgressBeishu);
		mSeekBarQishu = (SeekBar) view.findViewById(R.id.buy_zixuan_seek_qishu);
		mSeekBarQishu.setOnSeekBarChangeListener(this);
		mSeekBarQishu.setProgress(iProgressQishu);

		mTextBeishu = (TextView) view.findViewById(R.id.buy_zixuan_text_beishu);
		mTextBeishu.setText("" + iProgressBeishu);
		mTextQishu = (TextView) view.findViewById(R.id.buy_zixuan_text_qishu);
		mTextQishu.setText("" + iProgressQishu);
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_subtract_beishu, -1,mSeekBarBeishu, true,view);
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_add_beishu, 1, mSeekBarBeishu,true,view);
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_subtract_qihao, -1,mSeekBarQishu, false,view);
		setSeekWhenAddOrSub(R.id.buy_zixuan_img_add_qishu, 1, mSeekBarQishu,false,view);
    }
	/**
	 * 第一次启动投注提示框
	 */
	public void initTouZhuDialog(){
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View v= inflater.inflate(R.layout.alert_dialog_touzhu_new, null);
		touZhuDialog = new AlertDialog.Builder(this).setTitle("您选择的是").create();
		touZhuDialog.show();
		initImageView(v);
		if(betAndGift.isZhui()){
			initZhuiJia(v);
		}
		issueText =(TextView) v.findViewById(R.id.alert_dialog_touzhu_textview_qihao);
		alertText =(TextView) v.findViewById(R.id.alert_dialog_touzhu_text_one);
		textZhuma =(TextView) v.findViewById(R.id.alert_dialog_touzhu_text_zhuma);
		addView.getCodeList().get(addView.getSize()-1).setTextCodeColor(textZhuma);
		textTitle = (TextView) v.findViewById(R.id.alert_dialog_touzhu_text_zhuma_title);
		textTitle.setText("注码："+"共有"+addView.getSize()+"笔投注");
		issueText.setText(PublicMethod.toIssue(betAndGift.getLotno())+"期");
		alertText.setText(getTouzhuAlertJixuan());
		Button cancel = (Button) v.findViewById(R.id.alert_dialog_touzhu_button_cancel);
		Button ok = (Button) v.findViewById(R.id.alert_dialog_touzhu_button_ok);
		codeInfo = (Button) v.findViewById(R.id.alert_dialog_touzhu_btn_look_code);
		isCodeText(codeInfo);
		codeInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addView.createCodeInfoDialog();
				addView.showDialog();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				touZhuDialog.cancel();
				toLogin = false;
				clearProgress();
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RWSharedPreferences pre = new RWSharedPreferences(DanshiJiXuan.this, "addInfo");
				sessionId = pre.getStringValue("sessionid");
				phonenum = pre.getStringValue("phonenum");
				userno = pre.getStringValue("userno");
				if (sessionId.equals("")) {
					toLogin = true;
					Intent intentSession = new Intent(DanshiJiXuan.this, UserLogin.class);
					startActivityForResult(intentSession, 0);
				} else {
					
				}
				touZhu();
			}

		});
		CheckBox checkPrize = (CheckBox) v.findViewById(R.id.alert_dialog_touzhu_check_prize);
		checkPrize.setChecked(true);
		checkPrize.setButtonDrawable(R.drawable.check_on_off);
		checkPrize.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					betAndGift.setPrizeend("1");
				}else{
					betAndGift.setPrizeend("0");
				}
			}
		});
		check = (RadioButton) v.findViewById(R.id.alert_dialog_touzhu_check);
		joinCheck = (RadioButton) v.findViewById(R.id.alert_dialog_join_check);
		touzhuCheck = (RadioButton) v.findViewById(R.id.alert_dialog_touzhu1_check);
		touzhuCheck.setChecked(true);
		textAlert = (TextView) v.findViewById(R.id.alert_dialog_touzhu_text_alert);
		check.setPadding(50, 0, 0, 0);
	    check.setButtonDrawable(R.drawable.check_select);
		// 实现记住密码 和 复选框的状态
	    check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
                    isGift = isChecked;
			}
		});
		joinCheck.setPadding(50, 0, 0, 0);
	    joinCheck.setButtonDrawable(R.drawable.check_select);
		// 实现记住密码 和 复选框的状态
	    joinCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
                    isJoin = isChecked;
			}
		});
		touzhuCheck.setPadding(50, 0, 0, 0);
	    touzhuCheck.setButtonDrawable(R.drawable.check_select);
		// 实现记住密码 和 复选框的状态
	    touzhuCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				    isTouzhu = isChecked;
			}
		});
		stateCheck();
		touZhuDialog.setCancelable(false);
		touZhuDialog.getWindow().setContentView(v);
	}
	/**
	 * 用户投注
	 */
	private void touZhu() {
		toLogin = false;
		initBet();
		touZhuDialog.cancel();
		// TODO Auto-generated method stub
		if(isGift){
			String code=addView.getsharezhuma();
			toActivity(code);
		}else if(isJoin){
			toJoinActivity();
		}else if(isTouzhu){
				touZhuNet();
		}
		clearProgress();
	}
	/**
	 * 显示追加投注
	 * @param view
	 */
	private void initZhuiJia(View view) {
		LinearLayout toggleLinear = (LinearLayout)view.findViewById(R.id.buy_zixuan_linear_toggle);
		toggleLinear.setVisibility(LinearLayout.VISIBLE);
		ToggleButton zhuijiatouzhu = (ToggleButton)view.findViewById(R.id.dlt_zhuijia);
		zhuijiatouzhu.setOnCheckedChangeListener(new OnCheckedChangeListener() {			

			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked){
					betAndGift.setAmt(3);
					betAndGift.setIssuper("0");
				}else{
					betAndGift.setIssuper("");
					betAndGift.setAmt(2);
				}
				addView.setCodeAmt(betAndGift.getAmt());
				alertText.setText(getTouzhuAlertJixuan());
			}
		});
	}
	/**
	 * 清空倍数和期数的进度条
	 */
	public void clearProgress(){
		iProgressBeishu = 1;
		iProgressQishu = 1;
		mSeekBarBeishu.setProgress(iProgressBeishu);
		mSeekBarQishu.setProgress(iProgressQishu);
	}
	/**
	 * 再次启动提示框
	 */
	public void initAlerDialog(){
		touzhuCheck.setChecked(true);
		clearProgress();
		stateCheck();
		issueText.setText(PublicMethod.toIssue(betAndGift.getLotno())+"期");
		textTitle.setText("注码："+"共有"+addView.getSize()+"笔投注");
		addView.getCodeList().get(addView.getSize()-1).setTextCodeColor(textZhuma);
		isCodeText(codeInfo);
		alertText.setText(getTouzhuAlertJixuan());
		touZhuDialog.show();
	}
	private void isCodeText(Button codeInfo) {
		if(addView.getSize()>1){
			codeInfo.setVisibility(Button.VISIBLE);
		}else{
			codeInfo.setVisibility(Button.GONE);
		}
	}
    /**
     * 判断期数是否大于1
     */
    public void stateCheck(){
		if(iProgressQishu>1){
			isGift = false;
			isJoin = false;
			isTouzhu = true;
			check.setVisibility(CheckBox.GONE);
			joinCheck.setVisibility(CheckBox.GONE);
			touzhuCheck.setVisibility(CheckBox.GONE);
			textAlert.setVisibility(TextView.VISIBLE);
		}else{
			check.setVisibility(CheckBox.VISIBLE);
			joinCheck.setVisibility(CheckBox.VISIBLE);
			touzhuCheck.setVisibility(CheckBox.VISIBLE);
			textAlert.setVisibility(TextView.GONE);
		}
    }
	public void toJoinActivity(){
		  clearAddView();
		  ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		   try {
			   ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
	           objStream.writeObject(betAndGift);
		  } catch (IOException e) {
			  return;  // should not happen, so donot do error handling
		  }
		  
		  Intent intent = new Intent(DanshiJiXuan.this,JoinStartActivity.class);
		  intent.putExtra("info", byteStream.toByteArray());
		  startActivity(intent); 


	}
	/**
	 * 赠送彩票跳转
	 * @param zhuma
	 */
	public void toActivity(String zhuma){
		  clearAddView();
		  ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		   try {
			   ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
	           objStream.writeObject(betAndGift);
		  } catch (IOException e) {
			  return;  // should not happen, so donot do error handling
		  }
		  
		  Intent intent = new Intent(DanshiJiXuan.this, GiftActivity.class);
		  intent.putExtra("info", byteStream.toByteArray());
		  intent.putExtra("zhuma", zhuma);
		  startActivity(intent); 


	}
	/**
	 * 投注联网
	 */
	public void touZhuNet(){
	
		showDialog(DIALOG1_KEY); // 显示网络提示框 2010/7/4
		// 加入是否改变切入点判断 陈晨 8.11
		Thread t = new Thread(new Runnable() {
			String str = "00";
			@Override
			public void run() {
					str = BetAndGiftInterface.getInstance().betOrGift(betAndGift);
					try {
						JSONObject obj = new JSONObject(str);		
						String msg = obj.getString("message");
						String error = obj.getString("error_code");
						handler.handleMsg(error,msg);
					} catch (JSONException e) {
						e.printStackTrace();
	
					}
					progressdialog.dismiss();
			}

		});
		t.start();
	}
	/**
	 * 初始化投注信息
	 */
	public void initBet(){
		betAndGift.setIsSellWays("1");//多注投
		betAndGift.setSessionid(sessionId);
		betAndGift.setPhonenum(phonenum);
		betAndGift.setUserno(userno);
		betAndGift.setBettype("bet");// 投注为bet,赠彩为gift 
		betAndGift.setLotmulti(""+iProgressBeishu);//lotmulti    倍数   投注的倍数
		betAndGift.setBatchnum(""+iProgressQishu);//batchnum    追号期数 默认为1（不追号）
		betAndGift.setBatchcode(PublicMethod.toIssue(betAndGift.getLotno()));
		betAndGift.setAmount(""+addView.getSize()*iProgressBeishu*betAndGift.getAmt()*100);// amount      金额 单位为分（总金额）
//		betAndGift.setBet_code(ballOne.getZhuma(balls, iProgressBeishu));
		betAndGift.setBet_code(addView.getTouzhuCode(iProgressBeishu,betAndGift.getAmt()*100));
		lotno = PublicMethod.toLotno(betAndGift.getLotno());
		betAndGift.setBatchcode(PublicMethod.toIssue(betAndGift.getLotno()));
		
	}
	/**
	 * 网络连接提示框
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG1_KEY: {
			progressdialog = new ProgressDialog(this);
			// progressdialog.setTitle("Indeterminate");
			progressdialog.setMessage("网络连接中...");
			progressdialog.setIndeterminate(true);
			progressdialog.setCancelable(true);
			return progressdialog;
		}
		}
		return null;
	}
	/**
	 * 实现震动的类
	 * @author Administrator
	 *
	 */
	class SsqSensor extends SensorActivity {

		public SsqSensor(Context context) {
			getContext(context);
		}

		@Override
		public void action() {
			zhumaView.removeAllViews();
			balls = new Vector<Balls>();
			for (int i = 0; i < jixuanZhu.getSelectedItemPosition() + 1; i++) {
				Balls ball = ballOne.createBalls();
				balls.add(ball);
			}
			createTable(zhumaView);
		}
	}
	/**
	 * 重新初始化界面
	 */
	public void againView(){
	    sensor.startAction();
	    sensor.onVibrator();// 震动
	    toast.show();
	    jixuanZhu.setSelection(4);
		zhumaView.removeAllViews();
		balls = new Vector();
		for (int i = 0; i < jixuanZhu.getSelectedItemPosition() + 1; i++) {
			Balls ball = ballOne.createBalls();
			balls.add(ball);
		}
		createTable(zhumaView);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
//			touZhu();
			break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}	
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);//BY贺思明 2012-7-24
		if(!toLogin){
		    againView();			
		}else{
			toLogin = false;
		}
	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);//BY贺思明 2012-7-24
		if(!toLogin){
			sensor.stopAction();
		}
	}
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	/* (non-Javadoc)
	 * @see com.ruyicai.handler.HandlerMsg#errorCode_0000()
	 */
	@Override
	public void errorCode_0000() {
		// TODO Auto-generated method stub
		String code=addView.getsharezhuma();
		clearAddView();
	    PublicMethod.showDialog(DanshiJiXuan.this,lotno+code);
	}
	private void clearAddView() {
		addView.clearInfo();
		addView.updateTextNum();
	}
	/* (non-Javadoc)
	 * @see com.ruyicai.handler.HandlerMsg#errorCode_000000()
	 */
	@Override
	public void errorCode_000000() {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see com.ruyicai.handler.HandlerMsg#getContext()
	 */
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}
	/**
	 * 退出提示
	 * 
	 * @param string
	 *            显示框信息
	 * @return
	 */
	public void alertExit(String string) {   
		Builder dialog = new AlertDialog.Builder(this).setTitle("温馨提示")
				.setMessage(string).setNeutralButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
                                    finish();
							}
						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
		dialog.show();

	}
	/**
	 * 重写放回建
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case 4:
			if(addView.getSize()!=0){
				alertExit(getString(R.string.buy_alert_exit));
			}else{
				finish();
			}
			break;
		}
		return false;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
}
