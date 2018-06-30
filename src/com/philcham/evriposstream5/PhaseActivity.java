package com.philcham.evriposstream5;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


public class PhaseActivity extends Activity{




/** Called when the activity is first created. */

 Button mback;
 TextView Phasearray[]= new TextView [5];
double MoonDistance,MoonAzimouth,MoonAltitude,SunDistance,SunAltitude,SunDeclinition,Angle;

 Calendar c;
  SimpleDateFormat yearf2;
 ScrollView Sc;
 float TestS;
 String[] PhaseAr2=new String [4];
TextView moon_RiseSet,moon_Dist,moon_Alt,moon_Decl,sun_RiseSet,sun_Dist,sun_Alt,sun_Decl,AngleView;
String moon_rise,moon_set,sun_rise,sun_set;

	  /** Called when the activity is first created. */
//ROUND A DOUBLE WITH TWO DECIMAL
double RoundTo2Decimals(double val){
	double f=Math.round(val*100);
	return f/100;
}
 //@SuppressWarnings("static-access")
@Override
public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.phaselayout);

     Phasearray[0]= (TextView) findViewById (R.id.New1);
     Phasearray[1]= (TextView) findViewById (R.id.Q1o);
     Phasearray[2]= (TextView) findViewById (R.id.Full);
     Phasearray[3]= (TextView) findViewById (R.id.Q2o);
     Phasearray[4]= (TextView) findViewById (R.id.New2);
     moon_RiseSet=(TextView) findViewById (R.id.MoonRiseSet);
     moon_Dist= (TextView) findViewById (R.id.MoonDist);
     moon_Alt= (TextView) findViewById (R.id.MoonAlt);
     moon_Decl= (TextView) findViewById (R.id.MoonDecl);
     sun_Dist= (TextView) findViewById (R.id.SunDist);
     sun_Alt= (TextView) findViewById (R.id.SunAlt);
     sun_Decl= (TextView) findViewById (R.id.SunDecl);
     sun_RiseSet=(TextView) findViewById (R.id.SunRiseSet);
     AngleView=(TextView) findViewById (R.id.Angle);
  
 
 Bundle B=getIntent().getExtras();
 if (B != null) {
 PhaseAr2=B.getStringArray("PhaseSend");
 moon_rise=B.getString("Moon_Rise");
 moon_set=B.getString("Moon_Set");
 MoonDistance=B.getDouble("MoonDistance");
 MoonAzimouth=B.getDouble("MoonAzimouth");
 MoonAltitude=B.getDouble("MoonAltitude");
 SunDistance=B.getDouble("SunDistance");
 SunAltitude=B.getDouble("SunAltitude");
 SunDeclinition=B.getDouble("SunDeclinition");
 sun_rise=B.getString("Sun_Rise");
 sun_set=B.getString("Sun_Set");
 Angle=B.getDouble("SMAngle");
 }


	Phasearray[0].setText(new StringBuilder().append("Νέα Σελήνη : "+PhaseAr2[0]));
	Phasearray[1].setText(new StringBuilder().append("1ο Τέταρτο : "+PhaseAr2[1]));
	Phasearray[2].setText(new StringBuilder().append("Πανσέληνος : "+PhaseAr2[2]));
	Phasearray[3].setText(new StringBuilder().append("2ο Τεταρτο : "+PhaseAr2[3]));
	Phasearray[4].setText(new StringBuilder().append("Νέα Σελήνη : "+PhaseAr2[4]));
	AngleView.setText(new StringBuilder().append("Η γωνία του ήλιου με την σελήνη τώρα: "+Angle+" μοίρες"));
	moon_RiseSet.setText(new StringBuilder().append("Ανατολή : "+moon_rise+" Δύση : "+moon_set));
	moon_Dist.setText(new StringBuilder().append("Απόσταση : "+RoundTo2Decimals(MoonDistance)+" Kms από το κέντρο της γης"));
	moon_Alt.setText(new StringBuilder().append(" Ύψος(Altitude): "+MoonAltitude+ " Μοίρες από τον θαλάσσιο ορίζοντα"));
	moon_Decl.setText(new StringBuilder().append(" Αζιμούθιο: "+MoonAzimouth+" Μοίρες από τον βορρά αριστερόστροφα"));
	sun_Dist.setText(new StringBuilder().append("Απόσταση : "+RoundTo2Decimals(SunDistance)+" Kms από το κέντρο της γης"));
	sun_Alt.setText(new StringBuilder().append(" Ύψος(Altitude): "+SunAltitude+ " Μοίρες από τον θαλάσσιο ορίζοντα"));
	sun_Decl.setText(new StringBuilder().append(" Αζιμούθιο: "+SunDeclinition+" Μοίρες από τον βορρά αριστερόστροφα"));
	sun_RiseSet.setText(new StringBuilder().append("Ανατολή : "+sun_rise+" Δύση : "+sun_set));

 }
}


    
