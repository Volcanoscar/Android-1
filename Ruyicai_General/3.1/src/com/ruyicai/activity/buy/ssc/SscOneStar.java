package com.ruyicai.activity.buy.ssc;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.palmdream.RuyicaiAndroid.R;
import com.ruyicai.activity.buy.ZixuanAndJiXuan;
import com.ruyicai.interfaces.BuyImplement;
import com.ruyicai.jixuan.Balls;
import com.ruyicai.jixuan.SscBalls;
import com.ruyicai.pojo.AreaNum;
import com.ruyicai.util.Constants;

public class SscOneStar extends ZixuanAndJiXuan {
	    public static SscOneStar self;
	    private boolean isjixuan=false;
	    public static final int SSC_TYPE = 1;//一星
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		lotnoStr = Constants.LOTNO_SSC;
		childtype= new String[]{"直选","机选"};
		setContentView(R.layout.sscbuyview);
		self = this;
		init();
		highttype = "SSC";
	}
	
	public void theMethodYouWantToCall(){
        // do what ever you want here
		init();
    }

	@Override
	protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	}
	//单选框切换直选，机选
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId){		
		case 0:
			isjixuan=false;
			iProgressBeishu = 1;iProgressQishu = 1;
			initArea();
			createView(areaNums, sscCode,ZixuanAndJiXuan.ONE,true);
			BallTable=areaNums[0].table;
		break;
		case 1:
			isjixuan=true;
			iProgressBeishu = 1;iProgressQishu = 1;
			SscBalls sscb  = new SscBalls(1);
			createviewmechine(sscb);
		break;	
		}
			
	}
    @Override
    protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    }

	public String isTouzhu() {
		String isTouzhu = "";
			int ge = BallTable.getHighlightBallNums();
			int iZhuShu = getZhuShu();
			if (ge == 0) {
				 isTouzhu = "请至少选择一注！";
			} else if (iZhuShu * 2 > 20000) {
				isTouzhu = "false";
			} else if (ge > 5) {
				 isTouzhu = "一星直选，小球的个数最大为5个！";
			} else {
//				setZhuShu(iZhuShu);
//				alertZX(getZxAlertZhuma()); 
				isTouzhu = "true";
			}
			
		return isTouzhu; 
	}
	public String getZxAlertZhuma() {
		int iZhuShu = getZhuShu();
		String zhuma_string="";
		int[]  ZhuMa = BallTable.getHighlightBallNOs();
		for (int i = 0; i < BallTable.getHighlightBallNOs().length; i++) {
			    zhuma_string = zhuma_string + String.valueOf(ZhuMa[i]);
			if (i != BallTable.getHighlightBallNOs().length - 1) {
				zhuma_string = zhuma_string + ",";
			}
		}
		return  "注码：" + "\n" + "个位：" + zhuma_string;
		
	}
		

   public int getZhuShu() {
	int iReturnValue = 0;
	if(isjixuan){
	iReturnValue = balls.size()*iProgressBeishu;
	}else{
	int ge = areaNums[0].table.getHighlightBallNums();
    iReturnValue = ge*iProgressBeishu;
    }
	
	return iReturnValue;
}   
   
   public String getZhuma(){
	   String zhuma="";
	   if(isjixuan){
	    zhuma = ballOne.getZhuma(balls, SSC_TYPE);   
	   }else{
	   zhuma = sscCode.zhuma(areaNums, iProgressBeishu, 0);
	   }
	   return zhuma;
   }
	@Override
	public String getZhuma(Balls ball) {
		   String zhuma="";
		   zhuma = ball.getZhuma(null, SSC_TYPE);   
		   return zhuma;
	}
	@Override
	public String textSumMoney(AreaNum[] areaNum, int iProgressBeishu) {
		// TODO Auto-generated method stub
		String iTempString;
		int iZhuShu = getZhuShu();
		if (iZhuShu != 0) {
		    iTempString = "共" + iZhuShu + "注，共" + (iZhuShu * 2) + "元";
		} else {
			iTempString = getResources().getString(R.string.please_choose_number);
		}
	
		return iTempString;
	}


	@Override
	public void touzhuNet() {
		// TODO Auto-generated method stub
		int zhuShu=getZhuShu();
		if(isjixuan){
		betAndGift.setSellway("1")	;
		}else{
		betAndGift.setSellway("0");}//1代表机选 0代表自选
		betAndGift.setLotno(Constants.LOTNO_SSC);
		betAndGift.setBet_code(getZhuma());
		betAndGift.setAmount(""+zhuShu*200);
		
	}



}
