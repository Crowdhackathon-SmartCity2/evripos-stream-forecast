package com.philcham.evriposstream5;
 //phase of the moon calculations
//
//Adapted from "moontool.c" by John Walker, Release 2.0.

//Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions
//are met:
//1. Redistributions of source code must retain the above copyright
//notice, this list of conditions and the following disclaimer.
//2. Redistributions in binary form must reproduce the above copyright
//notice, this list of conditions and the following disclaimer in the
//documentation and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
//FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
//OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
//HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
//LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
//OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
//SUCH DAMAGE.
//
//Visit the ACME Labs Java page for up-to-date versions of this and other
//fine Java utilities: http://www.acme.com/java/


//import java.sql.Date;
//import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//import android.os.Build;
import android.os.Bundle;
//import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.ScrollView;
import android.widget.TimePicker;

//@TargetApi(Build.VERSION_CODES.KITKAT)
public class MainActivity extends Activity {
private Button BDate,BTime,Binfo,BPhase;
private EditText FText,F2Text;
//private ScrollView ScrolBut;
private Calendar c, cfull,c1o,c2o,cnew1,cnew2;
private Date Cdate, DateNext, SetRiseTime;
private double LocalLatitude=38.46723021032923;//LATITUDE ΠΑΡΑΤΗΡΗΤΗ
private double LocalLong=-23.591315746307373;//LONGITUDE ΠΑΡΑΤΗΡΗΤΗ
private double SunDecl,SunEarthLong,SunAlt,SunDistance,Logos, AntimLong,Wf,Wl,AWf,SetL,AngleSM,MaxW;
private double MOONDIST = 0, DiffSM,DiffSaM,SmeanAnom;
private int iMrise,iMset,iSrise,iSset,year,month,day,hour,minute,RiseCorect,SetCorect; //TimeN υπολογισμος ροής απο λιμενικο
private int [] f={70,165,250,345};//{100,8,-75,-172}
private long[] DNext=new long[1445];
private Bitmap mutableBitmap,bitmap;
private Canvas canvas;
private double[] MoonRise=new double[1441];
private double[] MSdif=new double[10];
private double[] SunRise=new double[1441];
private Paint pWhite=new Paint();
private Paint pBlue=new Paint();
private Paint pYellow=new Paint();
private Paint pBlack=new Paint();
private Paint pRed=new Paint();
private Paint pOrange=new Paint();
private Paint pGreen=new Paint();
private String StrSlong, StrSlat, StrMlat,StrMlog,Katastasi,fora ; 
private double test[]=new double [5];
private String PhaseArray[]=new String [5];
private double  TJD,Ttime,MoonDecl;
long MoonHrs = 0,MoonMins=0,MoonDays=0;
private ImageView image1;
private int BitmapWidth,BitmapHeight;
private SimpleDateFormat yearf;
final Context context = this;
static final int DATE_PICKER_ID = 1111; 
static final int TIME_PICKER_ID = 2222; 
//eccentricity of Earth's orbit
private static final double eccent = 0.016718D;
//semi-major axis of Earth's orbit, km
private static final double sunsmax = 1.495985e8D;
//synodic month (new Moon to new Moon)
private static final double synmonth = 29.53058868D;
//Fix angle.
private double fixangle( double a )
	{
	double b = a - 360.0 * Math.floor( a / 360.0D );
	return b;
	}
public class RefInt
{
public int val;
public RefInt()
	{
	}
public RefInt( int val )
	{
	this.val = val;
	}
}
public class RefDouble
{
public double val;
public RefDouble()
	{
	}
public RefDouble( double val )
	{
	this.val = val;
	}
}
//Degrees to radians.
private  double torad( double d )
	{
	return d * Math.PI / 180.0D;
	}

//Radians to degrees.
private double todeg( double r )
	{
	return r * 180.0D / Math.PI;
	}

//Sin from degrees.
private  double dsin( double d )
	{
	return Math.sin( torad( d ) );
	}

//Cos from degrees.
private  double dcos( double d )
	{
	return Math.cos( torad( d ) );
	}
//meanphase - calculates mean phase of the Moon for a given base date
//and desired phase:
//0.0   New Moon
//0.25  First quarter
//0.5   Full moon
//0.75  Last quarter
//Beware!!!  This routine returns meaningless results for any other
//phase arguments.  Don't attempt to generalise it without understanding
//that the motion of the moon is far more complicated that this
//calculation reveals.

private  double meanphase( double sdate, double phase, RefDouble usek )
	{
	RefInt yy = new RefInt();

	RefInt mm = new RefInt();
	RefInt dd =  new RefInt();

	double k, t, t2, t3, nt1;

	jyear( sdate, yy, mm, dd );

	k = (yy.val + ((mm.val - 1) * (1.0 / 12.0)) - 1900) * 12.3685;
	//k = (yy + ((mm - 1) * (1.0 / 12.0)) - 1900) * 12.3685;

	// Time in Julian centuries from 1900 January 0.5.
	t = (sdate - 2415020.0) / 36525;
	t2 = t * t;		   // square for frequent use
	t3 = t2 * t;		   // cube for frequent use

	usek.val = k = Math.floor(k) + phase;
	
	nt1 = 2415020.75933 + synmonth * k
	      + 0.0001178 * t2
	      - 0.000000155 * t3
	      + 0.00033 * dsin(166.56 + 132.87 * t - 0.009173 * t2);

	return nt1;
	}
public void jyear(double td, RefInt yy2, RefInt mm2, RefInt dd) {

double j, d, y, m;

td += 0.5;	// astronomical to civil
j = Math.floor(td);
j = j - 1721119.0;
y = Math.floor(((4 * j) - 1) / 146097.0);
j = (j * 4.0) - (1.0 + (146097.0 * y));
d = Math.floor(j / 4.0);
j = Math.floor(((4.0 * d) + 3.0) / 1461.0);
d = ((4.0 * d) + 3.0) - (1461.0 * j);
d = Math.floor((d + 4.0) / 4.0);
m = Math.floor(((5.0 * d) - 3) / 153.0);
d = (5.0 * d) - (3.0 + (153.0 * m));
d = Math.floor((d + 5.0) / 5.0);
y = (100.0 * y) + j;
if (m < 10.0)
  m = m + 3;
else
  {
  m = m - 9;
  y = y + 1;
  }
yy2.val = (int) y;
mm2.val = (int) m;
dd.val = (int) d;
}


//truephase - given a K value used to determine the mean phase of the
//   new moon, and a phase selector (0.0, 0.25, 0.5, 0.75),
//   obtain the true, corrected phase time
private  double truephase( double k, double phase )
	{
	double t, t2, t3, pt, m, mprime, f;
	boolean apcor = false;

	k += phase;		   /* add phase to new moon time */
	t = k / 1236.85;	   /* time in Julian centuries from
				        1900 January 0.5 */
	t2 = t * t;		   /* square for frequent use */
	t3 = t2 * t;		   /* cube for frequent use */
	pt = 2415020.75933	   /* mean time of phase */
	     + synmonth * k
	     + 0.0001178 * t2
	     - 0.000000155 * t3
	     + 0.00033 * dsin(166.56 + 132.87 * t - 0.009173 * t2);

  m = 359.2242               /* Sun's mean anomaly */
	    + 29.10535608 * k
	    - 0.0000333 * t2
	    - 0.00000347 * t3;
  SmeanAnom=m;
  mprime = 306.0253          /* Moon's mean anomaly */
	    + 385.81691806 * k
	    + 0.0107306 * t2
	    + 0.00001236 * t3;
  f = 21.2964                /* Moon's argument of latitude */
	    + 390.67050646 * k
	    - 0.0016528 * t2
	    - 0.00000239 * t3;
	if ((phase < 0.01) || (Math.abs(phase - 0.5) < 0.01))
	    {
	    /* Corrections for New and Full Moon. */
	    pt +=     (0.1734 - 0.000393 * t) * dsin(m)
		     + 0.0021 * dsin(2 * m)
		     - 0.4068 * dsin(mprime)
		     + 0.0161 * dsin(2 * mprime)
		     - 0.0004 * dsin(3 * mprime)
		     + 0.0104 * dsin(2 * f)
		     - 0.0051 * dsin(m + mprime)
		     - 0.0074 * dsin(m - mprime)
		     + 0.0004 * dsin(2 * f + m)
		     - 0.0004 * dsin(2 * f - m)
		     - 0.0006 * dsin(2 * f + mprime)
		     + 0.0010 * dsin(2 * f - mprime)
		     + 0.0005 * dsin(m + 2 * mprime);
	    apcor = true;
	    }
	else if ((Math.abs(phase - 0.25) < 0.01 || (Math.abs(phase - 0.75) < 0.01)))
	    {
	    pt +=     (0.1721 - 0.0004 * t) * dsin(m)
		     + 0.0021 * dsin(2 * m)
		     - 0.6280 * dsin(mprime)
		     + 0.0089 * dsin(2 * mprime)
		     - 0.0004 * dsin(3 * mprime)
		     + 0.0079 * dsin(2 * f)
		     - 0.0119 * dsin(m + mprime)
		     - 0.0047 * dsin(m - mprime)
		     + 0.0003 * dsin(2 * f + m)
		     - 0.0004 * dsin(2 * f - m)
		     - 0.0006 * dsin(2 * f + mprime)
		     + 0.0021 * dsin(2 * f - mprime)
		     + 0.0003 * dsin(m + 2 * mprime)
		     + 0.0004 * dsin(m - 2 * mprime)
		     - 0.0003 * dsin(2 * m + mprime);
	    if (phase < 0.5)
	        /* First quarter correction. */
	        pt += 0.0028 - 0.0004 * dcos(m) + 0.0003 * dcos(mprime);
	    else
	        /* Last quarter correction. */
	        pt += -0.0028 + 0.0004 * dcos(m) - 0.0003 * dcos(mprime);
	    apcor = true;
	    }
	if (!apcor)

		throw new InternalError( "Ιnvalid phase selector" );
	return pt;
	}




/// Calculate phase of moon as a fraction.
//<P>
//@param pdate time for which the phase is requested, as from jtime()
//@param pphaseR Ref for illuminated fraction of Moon's disk
//@param mageR Ref for age of moon in days
//@param distR Ref for distance in km from center of Earth
//@param angdiaR Ref for angular diameter in degrees as seen from Earth
//@param sudistR Ref for distance in km to Sun
//@param suangdiaR Ref for Sun's angular diameter
//@return terminator phase angle as a fraction of a full circle (i.e., 0 to 1)
//

//{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{

//Μετατροπή σε  Ιουλιανή ημερομηνία JD = Ιδιο με το Jdate
public  double JDtime( Date t )
{//=INT(365,25*(Y+4716))+INT(30,6001*(M+1))+D+B-1524,5
double Ty,Tm,Td,Thour,Tmin,A,B,Timecorrection = 0;
Ty=1900+t.getYear();
Tm=1+t.getMonth();//0 ΙΑΝ 1 ΦΕΒ 2 ΜΑΡ 3 ΑΠΡ 4 ΜΑΙ 5 ΙΟΥΝ 6 ΙΟΥΛ 7 ΑΥΓ 8 ΣΕΠ 9 ΟΚΤ 10 ΝΟΕ 11 ΔΕΚ
if (t.getMonth()>=0 && t.getMonth()<=2){Timecorrection=2;} //ΑΠΟ ΙΑΝ=0 ΜΕΧΡΙ ΜΑΡ=2
//if ( t.getMonth()==2){Timecorrection=2;}

if (t.getYear()==115||t.getYear()==109||t.getYear()==120||t.getYear()==126){
if ( t.getMonth()==2  && t.getDate()<29){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=29){Timecorrection=3;}
}
if (t.getYear()==103||t.getYear()==108||t.getYear()==114||t.getYear()==125){
if ( t.getMonth()==2  && t.getDate()<30){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=30){Timecorrection=3;}//
}
if (t.getYear()==102||t.getYear()==113||t.getYear()==119||t.getYear()==124||t.getYear()==130){
if ( t.getMonth()==2  && t.getDate()<31){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=31){Timecorrection=3;}
}
if (t.getYear()==101||t.getYear()==107||t.getYear()==112||t.getYear()==118||t.getYear()==129){
if ( t.getMonth()==2  && t.getDate()<25){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=25){Timecorrection=3;}
}
if (t.getYear()==100||t.getYear()==106||t.getYear()==117||t.getYear()==123||t.getYear()==128){
if ( t.getMonth()==2  && t.getDate()<26){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=26){Timecorrection=3;}
}
if (t.getYear()==105||t.getYear()==111||t.getYear()==116||t.getYear()==122){
if ( t.getMonth()==2  && t.getDate()<27){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=27){Timecorrection=3;}
}
if (t.getYear()==104||t.getYear()==110||t.getYear()==121||t.getYear()==127){
if ( t.getMonth()==2  && t.getDate()<28){Timecorrection=2;}
if ( t.getMonth()==2  && t.getDate()>=28){Timecorrection=3;}
}
if (t.getMonth()>2 && t.getMonth()<9){Timecorrection=3;}// ΑΠΟ ΑΠΡ=3 ΜΕΧΡΙ ΣΕΠ=8


if ( t.getMonth()==9 ){Timecorrection=3;} //ΟΚΤΩΒΡΙΟΣ = 9

if (t.getYear()==115||t.getYear()==109||t.getYear()==120||t.getYear()==126){
if ( t.getMonth()==9  && t.getDate()<25){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=25){Timecorrection=2;}
}
if (t.getYear()==103||t.getYear()==108||t.getYear()==114||t.getYear()==125){
if ( t.getMonth()==9  && t.getDate()<26){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=26){Timecorrection=2;}
}
if (t.getYear()==102||t.getYear()==113||t.getYear()==119||t.getYear()==124||t.getYear()==130){
if ( t.getMonth()==9  && t.getDate()<27){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=27){Timecorrection=2;}
}
if (t.getYear()==101||t.getYear()==107||t.getYear()==112||t.getYear()==118||t.getYear()==129){
if ( t.getMonth()==9  && t.getDate()<28){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=28){Timecorrection=2;}
}
if (t.getYear()==100||t.getYear()==106||t.getYear()==117||t.getYear()==123||t.getYear()==128){
if ( t.getMonth()==9  && t.getDate()<29){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=29){Timecorrection=2;}
}
if (t.getYear()==105||t.getYear()==111||t.getYear()==116||t.getYear()==122){
if ( t.getMonth()==9  && t.getDate()<30){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=30){Timecorrection=2;}
}
if (t.getYear()==104||t.getYear()==110||t.getYear()==121||t.getYear()==127){
if ( t.getMonth()==9  && t.getDate()<31){Timecorrection=3;}
if ( t.getMonth()==9  && t.getDate()>=31){Timecorrection=2;}
}
if (t.getMonth()>=10 && t.getMonth()<=11){Timecorrection=2;}//ΑΠΟ ΝΟΕ=10 ΜΕΧΡΙ ΚΑΙ ΔΕΚ = 11
Thour=t.getHours()-Timecorrection;
int Daycorrection=0;
if (t.getMonth()==0){Daycorrection=1;}//ΔΙΟΡΘΩΝΕΙ ΤΟ BUG ΤΟΥ ΙΑΝΟΥΑΡΙΟΥ ΚΑΙ ΦΕΒΡΟΥΑΡΙΟΥ
if (t.getMonth()==1){Daycorrection=2;}//
Thour=t.getHours()-Timecorrection;

Tmin=t.getMinutes();
Td=t.getDate()+Daycorrection+(Thour+Tmin/60)/24;
A=Math.floor(Ty/100);
B=2-A+Math.floor(A/4);
return TJD=Math.floor(365.25*(Ty+4716))+Math.floor(30.6001*(Tm+1))+Td+B-1524.5;

}

//Υπολογισμός Τ=(JD-2451545,0)/36525

public double TtimeC (double Tjd2)
{
	return Ttime= (Tjd2-2451545.0)/36525.0;
}

//Υπολογισμός Δυνάμεων του Τ
public double T2 ()
{
	return Ttime*Ttime;
}
public double T3 ()
{
	return Ttime*Ttime*Ttime;
}
public double T4 ()
{
	return Ttime*Ttime*Ttime*Ttime;
}


//Υπολογισμός Moon's Mean Longitude (L'), refered to mean equinox of the date
public double MoonL () {
	return fixangle(218.3164477+481267.88123421*Ttime-0.0015786*T2()+T3()/538841-T4()/65194000);
}
//Υπολογισμός Moon's Mean elongation (D)
public double MoonD () {
	//return fixangle(297.8501921+445267.1114034*Ttime-0.0018819*T2()+T3()/545868-T4()/113065000);
	return fixangle(297.85036+445267.111480*Ttime-0.0019142*T2()+T3()/189474);
}
//Υπολογισμός Sun's Mean anomaly (M)
public double SunM () {
	//return fixangle(357.5291092+35999.0502909*Ttime-0.0001536*T2()+T3()/24490000);
	return fixangle(357.52772+35999.050340*Ttime-0.0001603*T2()+T3()/300000);
	//return SmeanAnom;
}
//Υπολογισμός Moon's Mean anomaly (M)
public double MoonM () {
	//return fixangle(134.9633964+477198.8675055*Ttime+0.0087414*T2()+T3()/69699-T4()/14712000);
	return fixangle(134.96298+477198.867398*Ttime+0.0086972*T2()+T3()/56250);
}
//Υπολογισμός Moon's argument of latitude (F)-mean distance of the moon from its ascending node
public double MoonF () {
	//return fixangle(93.2720950+483202.0175233*Ttime-0.0036539*T2()-T3()/3526000+T4()/863310000);
	return fixangle(93.27191+483202.017538*Ttime-0.0036825*T2()-T3()/327270);
}

//Υπολογισμός Moon's mean equinox of the Date
public double MEquin () {
	
	return fixangle(125.04452+1934.136261*Ttime-0.0020708*T2()-T3()/450000);
}
//Πινακας περιοδικών όρων για το Σl


//Υπολογισμός υπολοιπων αναγκαίων όρων Α1,Α2,Α3
public double A1 () {
	return fixangle(119.75+131.849*Ttime);
}
public double A2 () {
	return fixangle(53.09+479264.290*Ttime);
}
public double A3 () {
	return fixangle(313.45+481266.484*Ttime);
}
//Υπολογισμός Earth's orbit eccentricity around the sun
public double EarthE () {
	return 1-0.002516*Ttime-0.0000074*T2();
	//return eccent;
}
public double EarthE2 () {
	return EarthE ()*EarthE ();
}
//Πινακας περιοδικών όρων για το Σl

public double Sl () {
	double[] sumSl =new double[60];
	double Drad,SMrad,mMrad,Frad,Slc=0,A1rad,A2rad,Lrad;
	Drad=torad(MoonD());
	SMrad=torad(SunM());
	mMrad=torad(MoonM());
	Lrad=torad(MoonL());
	Frad=torad(MoonF());
	A1rad=torad(A1());
	A2rad=torad(A2());
	//A3rad=torad(A3());
	
	sumSl[1]=6288774*	Math.sin(mMrad);
	sumSl[2]=1274027*	Math.sin(2*Drad-mMrad);
	sumSl[3]=658314*	Math.sin(2*Drad);
	sumSl[4]=213618*	Math.sin(2*mMrad);
	sumSl[5]=-185116*EarthE()*	Math.sin(SMrad);
	sumSl[6]=-114332*	Math.sin(2*Frad);
	sumSl[7]=58793*		Math.sin(2*Drad-2*mMrad);
	sumSl[8]=57066*	EarthE()*	Math.sin(2*Drad-SMrad-mMrad);
	sumSl[9]=53322*		Math.sin(2*Drad+mMrad);
	sumSl[10]=45758*	Math.sin(2*Drad-SMrad);
	sumSl[11]=-4092*EarthE()*	Math.sin(SMrad-mMrad);
	sumSl[12]=-34720*	Math.sin(Drad);
	sumSl[13]=-30383*EarthE()*	Math.sin(SMrad+mMrad);
	sumSl[14]=15327*	Math.sin(2*Drad-2*Frad);
	sumSl[15]=-12528*	Math.sin(mMrad+2*Frad);
	sumSl[16]=10980*	Math.sin(mMrad-2*Frad);
	sumSl[17]=10675*	Math.sin(4*Drad-mMrad);
	sumSl[18]=10034*	Math.sin(3*mMrad);
	sumSl[19]=8548*		Math.sin(4*Drad-2*mMrad);
	sumSl[20]=-7888*EarthE()*	Math.sin(2*Drad+SMrad-mMrad);
	sumSl[21]=-6766*EarthE()*	Math.sin(2*Drad+SMrad);
	sumSl[22]=-5163*	Math.sin(Drad-mMrad);
	sumSl[23]=4987*EarthE()*		Math.sin(Drad+SMrad);
	sumSl[24]=4036*EarthE()*		Math.sin(2*Drad-SMrad+mMrad);
	sumSl[25]=3994*		Math.sin(2*Drad+2*mMrad);
	sumSl[26]=3861*		Math.sin(4*Drad);
	sumSl[27]=3665*		Math.sin(2*Drad-3*mMrad);
	sumSl[28]=-2689*EarthE()*	Math.sin(SMrad-2*mMrad);
	sumSl[29]=-2602*	Math.sin(2*Drad-mMrad+2*Frad);
	sumSl[30]=2390*	EarthE()*	Math.sin(2*Drad-SMrad-2*mMrad);
	sumSl[31]=-2348*	Math.sin(Drad+mMrad);
	sumSl[32]=2236*EarthE2()*		Math.sin(2*Drad-2*SMrad);
	sumSl[33]=-2120*EarthE()*	Math.sin(SMrad+2*mMrad);
	sumSl[34]=-2069*EarthE2()*	Math.sin(2*SMrad);
	sumSl[35]=2048*EarthE2()*		Math.sin(2*Drad-2*SMrad-mMrad);
	sumSl[36]=-1773*	Math.sin(2*Drad+mMrad-2*Frad);
	sumSl[37]=-1595*	Math.sin(2*Drad+2*Frad);
	sumSl[38]=1215*EarthE()*		Math.sin(4*Drad-SMrad-mMrad);
	sumSl[39]=-1110*	Math.sin(2*mMrad+2*Frad);
	sumSl[40]=-892*		Math.sin(3*Drad-mMrad);
	sumSl[41]=-810*EarthE()*		Math.sin(2*Drad+SMrad+mMrad);
	sumSl[42]=759*EarthE()*		Math.sin(4*Drad-SMrad-2*mMrad);
	sumSl[43]=-713*EarthE2()*		Math.sin(2*SMrad-mMrad);
	sumSl[44]=-700*EarthE()*		Math.sin(2*Drad+2*SMrad-mMrad);
	sumSl[45]=691*EarthE()*		Math.sin(2*Drad+SMrad-2*mMrad);
	sumSl[46]=596*EarthE()*		Math.sin(2*Drad-SMrad-2*Frad);
	sumSl[47]=549*		Math.sin(4*Drad+mMrad);
	sumSl[48]=537*		Math.sin(4*mMrad);
	sumSl[49]=520*EarthE()*		Math.sin(4*Drad-SMrad);
	sumSl[50]=-487*		Math.sin(Drad-2*mMrad);
	sumSl[51]=-399*EarthE()*		Math.sin(2*Drad+SMrad-2*Frad);
	sumSl[52]=-381*		Math.sin(2*mMrad-2*Frad);
	sumSl[53]=351*EarthE()*		Math.sin(Drad+SMrad+mMrad);
	sumSl[54]=-340*		Math.sin(3*Drad-2*mMrad);
	sumSl[55]=330*		Math.sin(4*Drad-3*mMrad);
	sumSl[56]=327*EarthE()*		Math.sin(2*Drad-SMrad+2*mMrad);
	sumSl[57]=-323*EarthE2()*		Math.sin(2*SMrad+mMrad);
	sumSl[58]=299*EarthE()*		Math.sin(Drad+SMrad-mMrad);
	sumSl[59]=294*		Math.sin(2*Drad+3*mMrad);
	
		for (int i=1;i<60;){Slc=Slc+sumSl[i];i++;}
		Slc=Slc+3958*Math.sin(A1rad);
		Slc=Slc+1962*Math.sin(Lrad-Frad);
		Slc=Slc+318*Math.sin(A2rad);
		
	return Slc;
}


public double Sr () {
	double[] sumSr = new double[48];
	double Drad,SMrad,mMrad,Frad,Src = 0;
	Drad=torad(MoonD());
	SMrad=torad(SunM());
	mMrad=torad(MoonM());
	Frad=torad(MoonF());
	
	sumSr[1]=-20905355*			Math.cos(mMrad);
	sumSr[2]=-3699111*			Math.cos(2*Drad-mMrad);
	sumSr[3]=-2955968*			Math.cos(2*Drad);
	sumSr[4]=-569925*			Math.cos(2*mMrad);
	sumSr[5]=48888*	EarthE()*	Math.cos(SMrad);
	sumSr[6]=-3149*				Math.cos(2*Frad);
	sumSr[7]=246158*			Math.cos(2*Drad-2*mMrad);
	sumSr[8]=-152138*EarthE()*	Math.cos(2*Drad-SMrad-mMrad);
	sumSr[9]=-170733*			Math.cos(2*Drad+mMrad);
	sumSr[10]=-204586*EarthE()*	Math.cos(2*Drad-SMrad);
	sumSr[11]=-129620*EarthE()*	Math.cos(SMrad-mMrad);
	sumSr[12]=108743*			Math.cos(Drad);
	sumSr[13]=104755*EarthE()*	Math.cos(SMrad+mMrad);
	sumSr[14]=10321*			Math.cos(2*Drad-2*Frad);
	sumSr[15]=79661*			Math.cos(mMrad-2*Frad);
	sumSr[16]=-34782*			Math.cos(4*Drad-mMrad);
	sumSr[17]=-23210*			Math.cos(3*mMrad);
	sumSr[18]=-21636*			Math.cos(4*Drad-2*mMrad);
	sumSr[19]=24208*EarthE()*	Math.cos(2*Drad+SMrad-mMrad);
	sumSr[20]=30824*EarthE()*	Math.cos(2*Drad+SMrad);
	sumSr[21]=-8379*	Math.cos(Drad-mMrad);
	sumSr[22]=-16675*EarthE()*	Math.cos(Drad+SMrad);
	sumSr[23]=-12831*EarthE()*	Math.cos(2*Drad-SMrad+mMrad);
	sumSr[24]=-10445*	Math.cos(2*Drad+2*mMrad);
	sumSr[25]=-11650*	Math.cos(4*Drad);
	sumSr[26]=14403*	Math.cos(2*Drad-3*mMrad);
	sumSr[27]=-7003*EarthE()*	Math.cos(SMrad-2*mMrad);
	sumSr[28]=10056*EarthE()*	Math.cos(2*Drad-SMrad-2*mMrad);
	sumSr[29]=6322*		Math.cos(Drad+mMrad);
	sumSr[30]=-9884*EarthE2()*	Math.cos(2*Drad-2*SMrad);
	sumSr[31]=5751*	EarthE()*	Math.cos(SMrad+2*mMrad);
	sumSr[32]=-4950*EarthE2()*	Math.cos(2*Drad-2*SMrad-mMrad);
	sumSr[33]=4130*		Math.cos(2*Drad+mMrad-2*Frad);
	sumSr[34]=-3958*EarthE()*	Math.cos(4*Drad-SMrad-mMrad);
	sumSr[35]=3258*		Math.cos(3*Drad-mMrad);
	sumSr[36]=2616*	EarthE()*	Math.cos(2*Drad+SMrad+mMrad);
	sumSr[37]=-1897*EarthE()*	Math.cos(4*Drad-SMrad-2*mMrad);
	sumSr[38]=-2117*EarthE2()*	Math.cos(2*SMrad-mMrad);
	sumSr[39]=2354*	EarthE2()*	Math.cos(2*Drad+2*SMrad-mMrad);
	sumSr[40]=-1423*	Math.cos(4*Drad+mMrad);
	sumSr[41]=-1117*	Math.cos(4*mMrad);
	sumSr[42]=-1571*EarthE()*	Math.cos(4*Drad-SMrad);
	sumSr[43]=-1739*	Math.cos(Drad-2*mMrad);
	sumSr[44]=-4421*	Math.cos(2*mMrad-2*Frad);
	sumSr[45]=1165*	EarthE2()*	Math.cos(2*SMrad+mMrad);
	sumSr[46]=8752*		Math.cos(2*Drad-mMrad-2*Frad);
	
		for (int i=1;i<47;){Src=Src+sumSr[i];i++;}
	return Src;
}

public double Sb () {

	double[] sumSb = new double[61];
	double Drad,SMrad,mMrad,Frad,Lrad,A1rad,A3rad,Sbc = 0;
	Drad=torad(MoonD());
	SMrad=torad(SunM());
	mMrad=torad(MoonM());
	Frad=torad(MoonF());
	Lrad=torad(MoonL());
	Frad=torad(MoonF());
	A1rad=torad(A1());
	
	A3rad=torad(A3());
	
	sumSb[1]=   5128122	*			Math.sin(Frad);
	sumSb[2]=   280602	*			Math.sin(mMrad+Frad);
	sumSb[3]=   277693	*		Math.sin(mMrad-Frad);
	sumSb[4]=   173237	*		Math.sin(2*Drad-Frad);
	sumSb[5]=   55413	*		Math.sin(2*Drad-mMrad+Frad);
	sumSb[6]=   46271	*		Math.sin(2*Drad-mMrad-Frad);
	sumSb[7]=   32573	*		Math.sin(2*Drad+Frad);
	sumSb[8]=   17198	*		Math.sin(2*mMrad+Frad);
	sumSb[9]=   9266	*		Math.sin(2*Drad+mMrad-Frad);
	sumSb[10]=  8822	*		Math.sin(2*mMrad-Frad);
	sumSb[11]=  8216	*EarthE()*		Math.sin(2*Drad-SMrad-Frad);
	sumSb[12]=  4324	*		Math.sin(2*Drad-2*mMrad-Frad);
	sumSb[13]=  4200	*		Math.sin(2*Drad+mMrad+Frad);
	sumSb[14]= -3359	*EarthE()*		Math.sin(2*Drad+SMrad-Frad);
	sumSb[15]=  2463	*EarthE()*		Math.sin(2*Drad-SMrad-mMrad+Frad);
	sumSb[16]=  2211	*EarthE()*	Math.sin(2*Drad-SMrad+Frad);
	sumSb[17]=  2065	*EarthE()*	Math.sin(2*Drad-SMrad-mMrad-Frad);
	sumSb[18]= -1870	*EarthE()*	Math.sin(SMrad-mMrad-Frad);
	sumSb[19]=	1828	*	Math.sin(4*Drad-mMrad-Frad);
	sumSb[20]= -1794	*EarthE()*	Math.sin(SMrad+Frad);
	sumSb[21]= -1749	*	Math.sin(3*Frad);
	sumSb[22]= -1565	*EarthE()*	Math.sin(SMrad-mMrad+Frad);
	sumSb[23]= -1491	*	Math.sin(Drad+Frad);
	sumSb[24]= -1475	*EarthE()*	Math.sin(SMrad+mMrad+Frad);
	sumSb[25]= -1410	*EarthE()*	Math.sin(SMrad+mMrad-Frad);
	sumSb[26]= -1344	*EarthE()*	Math.sin(SMrad-Frad);
	sumSb[27]= -1335	*	Math.sin(Drad-Frad);
	sumSb[28]=  1107	*	Math.sin(3*mMrad+Frad);
	sumSb[29]=  1021	*	Math.sin(4*Drad-Frad);
	sumSb[30]=  833		*	Math.sin(4*Drad-mMrad+Frad);
	sumSb[31]=  777		*	Math.sin(mMrad-3*Frad);
	sumSb[32]=  671		*	Math.sin(4*Drad-2*mMrad+Frad);
	sumSb[33]=  607		*	Math.sin(2*Drad-3*Frad);
	sumSb[34]=  596		*	Math.sin(2*Drad+2*mMrad-Frad);
	sumSb[35]=  491		*EarthE()*	Math.sin(2*Drad-SMrad+mMrad-Frad);
	sumSb[36]= -451		*	Math.sin(2*Drad-2*mMrad+Frad);
	sumSb[37]=  439		*	Math.sin(3*mMrad-Frad);
	sumSb[38]=  422		*	Math.sin(2*Drad+2*mMrad+Frad);
	sumSb[39]=  421		*	Math.sin(2*Drad-3*mMrad-Frad);
	sumSb[40]= -366		*EarthE()*	Math.sin(2*Drad+SMrad-mMrad+Frad);
	sumSb[41]= -351		*EarthE()*	Math.sin(2*Drad+SMrad+Frad);
	sumSb[42]=  331		*	Math.sin(4*Drad+Frad);
	sumSb[43]=  315		*EarthE()*	Math.sin(2*Drad-SMrad+mMrad+Frad);
	sumSb[44]=  302		*EarthE2()*	Math.sin(2*Drad-2*SMrad-Frad);
	sumSb[45]= -283		*	Math.sin(mMrad+3*Frad);
	sumSb[46]= -229		*EarthE()*	Math.sin(2*Drad+SMrad+mMrad-Frad);
	sumSb[47]=  223		*EarthE()*	Math.sin(Drad+SMrad-Frad);
	sumSb[48]=  223		*EarthE()*	Math.sin(Drad+SMrad+Frad);
	sumSb[49]= -220		*EarthE()*	Math.sin(SMrad-2*mMrad-Frad);
	sumSb[50]= -220		*EarthE()*	Math.sin(2*Drad+SMrad-mMrad-Frad);
	sumSb[51]= -185		*	Math.sin(Drad+mMrad+Frad);
	sumSb[52]=  181		*EarthE()*	Math.sin(2*Drad-SMrad-2*mMrad-Frad);
	sumSb[53]= -177		*EarthE()*	Math.sin(SMrad+2*mMrad+Frad);
	sumSb[54]=  176		*	Math.sin(4*Drad-2*mMrad-Frad);
	sumSb[55]=  166		*EarthE()*	Math.sin(4*Drad-SMrad-mMrad-Frad);
	sumSb[56]= -164		*	Math.sin(Drad+mMrad-Frad);
	sumSb[57]=  132		*	Math.sin(4*Drad+mMrad-Frad);
	sumSb[58]= -119		*	Math.sin(Drad-mMrad-Frad);
	sumSb[59]=  115		*EarthE()*	Math.sin(4*Drad-SMrad-Frad);
	sumSb[60]=  107		*EarthE2()*	Math.sin(2*Drad-2*SMrad+Frad);
							
		for (int i=1;i<61;){Sbc=Sbc+sumSb[i];i++;}
		Sbc=Sbc-2235*Math.sin(Lrad);
		Sbc=Sbc+382*Math.sin(A3rad);
		Sbc=Sbc+175*Math.sin(A1rad-Frad);
		Sbc=Sbc+175*Math.sin(A1rad+Frad);
		Sbc=Sbc+127*Math.sin(Lrad-mMrad);
		Sbc=Sbc-115*Math.sin(Lrad+mMrad);
		
	return Sbc;
}
//Geocentric (Ecliptic) moon Longitude MoonGeocLong

public double MoonEclipticLong(){
	
	return  MoonL()+Sl()/1000000;
}
//Geocentric moon Longitude MoonGeoLat     ;

public double MoonEclipticLat(){
	
	return Sb()/1000000;
}
//Μετατροπή Geocentric Longitude and Latitude to Righr Ascending and Declinition

//Obliquity of earth
public double eps (){
	return fixangle(23.0 + 26.0/60.0 + 21.448/3600.0 - (46.8150/3600.0)*Ttime- (0.00059/3600.0)*T2()+ (0.001813/3600.0)*T3());
}

public double RightAsc() {
double epstoRad; //obliquity of ecliptic:
double LatRad,LongRad,SinMoonDecl,xRA,yRA;
LatRad=torad(MoonEclipticLat());
LongRad=torad(MoonEclipticLong());

		
		epstoRad=torad(eps());

		SinMoonDecl=Math.sin(LatRad)*Math.cos(epstoRad)+Math.cos(LatRad)*Math.sin(epstoRad)*Math.sin(LongRad);
		MoonDecl=(180/Math.PI)*Math.asin(SinMoonDecl);
		//Από την εξίσωση tan(RA)=[sin(ecliptical-selestial longitude)*cos(obliquity of ecliptic)-tan(ecliptical-selestial lattitude)*sin(obliquity of ecliptic)]/cos(ecliptical-selestial longitude)
		xRA=torad(Math.sin(LongRad)*Math.cos(epstoRad)-Math.tan(LatRad)*Math.sin(epstoRad));
		yRA=torad(Math.cos(LongRad));
		return fixangle(todeg(Math.atan2(xRA,yRA)));//από -90 μέχρι +90 0 RA = 0.8328 RA MOBILE 3.685 RA = 0.0017965 RA MOBILE
}

//Compute mean sidereal time at Greenwich (according to: Jean Meeus: Astronomical Algorithms)
public double MoonsideralT () {
	double MeanSidT; //MEAN SIDERAL TIME AT GREENWICH θΟ		
	MeanSidT =fixangle( 280.46061837 + 360.98564736629*(TJD-2451545.0) + 0.000387933*T2() - T3()/38710000.0); // degrees
	return MeanSidT;
}

//Compute Local Hour Angle LocalHrAngl
public double LocalHrAngl() {
	
	return MoonsideralT()-LocalLong-RightAsc(); 
}
//Compute Local Azimouth
public double Azimouth() {//measured westward from SOUTH 90 ΜΟΙΡΕΣ WEST 180 NORTH 270 EAST- ΑΦΑΙΡΟΝΤΑΣ 180 ΜΟΙΡΕΣ ΤΟ ΜΕΤΡΑΜΕ ΑΠΟ NORTH 0 ΜΟΙΡΕΣ EAST 90 MOIΡΕΣ SOUTH 180 ΜΟΙΡΕΣ WEST 270
	
	double LHArad,Latrad,Declrad,xAz,yAz;
	LHArad=torad(LocalHrAngl());
	Latrad=torad(LocalLatitude);
	Declrad=torad(MoonDecl);
//Από την εξίσωση tan(Azimouth)=(sin(Local Hour Angle)/[cos(Local Hour Angle)*sin(Observer Lattitude)-tan(Declinition)*cos(Observer Lattitude)
	xAz=torad(Math.sin(LHArad));
	yAz=torad((Math.cos(LHArad)*Math.sin(Latrad)-Math.tan(Declrad)*Math.cos(Latrad)));
return fixangle((todeg(Math.atan2(xAz, yAz)))+180);
}
//Compute Local Altitude
public double Altitude() {//positive above the horizon, negative below

	double LHArad,Latrad,Declrad,sinAlt;
	LHArad=torad(LocalHrAngl());
	Latrad=torad(LocalLatitude);
	Declrad=torad(MoonDecl);
//Από την εξίσωση sin(Altitude)=sin(observer Lattitude)*sin(Declinition)+cos(observer Latitude)*cos(Declination)*cos(Local Hour Angle)

	sinAlt=((Math.sin(Latrad)*Math.sin(Declrad)+Math.cos(Latrad)*Math.cos(Declrad)*Math.cos(LHArad)));

return todeg(Math.asin(sinAlt));
}
public double MoonLogitude() {//ο κάθετος μεσημβρηνός που βρισκεται  σελήνη
double mLong;
mLong=RoundTo2Decimals(fixangle(LocalHrAngl()+LocalLong));
if (mLong>180) {mLong=mLong-360;}
if (mLong>0) {AntimLong=mLong-180;}
if (mLong<0) {AntimLong=mLong+180;}
if (mLong==0) {AntimLong=180;}
return mLong;
}

//Calculation of the Sun's position.
public void SunsLongitude( double pdate){
	double SunRA,SunLocalHrAngl;
	double Lo,etorad,C,V;
	double LHArad,Latrad,Declrad,Vrad;

	Lo=280.46646+36000.76983*Ttime+0.0003032*T2();//Suns geometric mean longitude refered to the mean anomaly of the earth
	//e=0.016708634-0.000042037* Ttime-0.0000001267*T2(); //Earth's orbit eccentricity
	//Suns equation of the center
	C=(1.914602-0.004817*Ttime-0.000014*T2())*Math.sin(torad(SunM()))+(0.019993-0.000101*Ttime)*Math.sin(torad(2*SunM()))+0.000289*Math.sin(torad(3*SunM()));
	//Suns true anomally
	//V=SunM()+C;
	V=SmeanAnom+C;
	Vrad=torad(V);
	//Sun-Earth and Moon-Eart centers distance 
  MOONDIST=385000.56+Sr()/1000;
	SunDistance=sunsmax/((1+eccent*Math.cos(Vrad)))/(1-eccent*eccent);
 
  Logos=0.0000000369415284062343*Math.pow((SunDistance/MOONDIST),3);//0.000000036939686290282
	//SunGeoCentricLong=fixangle(Lo+C);//=Suns Geocentric Longitude 0 μοιρες στο εαρινό ηλιοστάσιο	//Calculate sun Ecliptic coordinates
	//SunEclipticLong=o+1.915*Math.sin(torad(SunM())+0.020*Math.sin(torad(2*SunM())));
	//Suns Ecliptic Latitude =0.00033 ~ 0.00
	//Calculate sun EQUATORIAL coordinates Riht ascension and Declination 
	etorad=torad(eps());//Obliquity of earth
		SunRA = fixangle(todeg(Math.atan2(Math.cos(etorad)*Math.sin(torad(Lo)),Math.cos(torad(Lo)))));
		SunDecl=(180/Math.PI)*Math.asin(Math.sin(etorad)*Math.sin(torad(Lo)));
		SunLocalHrAngl=MoonsideralT()-LocalLong-SunRA;
		LHArad=torad(SunLocalHrAngl);
		Latrad=torad(LocalLatitude);
		Declrad=torad(SunDecl);
		SunEarthLong=fixangle(SunLocalHrAngl+LocalLong);

		if (SunEarthLong>180) {SunEarthLong=SunEarthLong-360;}
		//if (SunEarthLong>0) {SunEarthLong=SunEarthLong-1.75;}
		//if (SunEarthLong<0) {SunEarthLong=SunEarthLong+1.75;}
		SunAlt=todeg(Math.asin(((Math.sin(Latrad)*Math.sin(Declrad)+Math.cos(Latrad)*Math.cos(Declrad)*Math.cos(LHArad)))));

}
//Συνισταμένη δύναμης ήλιου και σελήνης
//	Fολ =Sqrt(fm2+fs2+2*fs*fm*cosφ) φ η γωνία μεταξύ των δύο δυνάμεων - επίσης fs=fm/2.2 και η γωνία ω μεταξύ 
//	της δύναμης της σελήνης και της συνισταμένης είναι tanω=tanφ*fm/(fm+fs)=>tanω=tanφ(fm/(fm+fm/2.2)=>tanω=0.6875*tanφ

public double W2(double Sl,double Ml){
	double sinf,cosf,W1,Ml2;
	Ml2=Ml;
	if (Sl<0) {Sl=Sl+360;}
	if (Ml<0) {Ml=Ml+360;}
	//DiffSM= Sl-Ml;// ΑΝ Η ΔΙΑΦΟΡΑ ΤΗ ΓΩΝΙΑΣ ΤΟΥ ΗΛΙΟΥ ΚΑΙ ΤΗΣ ΣΕΛΗΝΗΣ ΕΙΝΑΙ ΜΕΓΑΛΥΤΕΡΗ ΑΠΟ 102 ΜΟΙΡΕΣ ΤΟΤΕ
	sinf=Math.sin(torad(Sl-Ml));
	cosf=(Logos+Math.cos(torad(Sl-Ml)));
		W1=Ml2+todeg(Math.atan2(sinf,cosf));
		//Wf=W1;
		if (W1>180){W1=W1-360;};
return W1;
}
public double WLat2(double SlatW,double Mlat){
	double AbsDiff,sinf,cosf,W1,Ml2,Wl=0;
	//Ml2=Mlat;
	//if (SlatW<0) {SlatW=SlatW+180;}
	//if (Mlat<0) {Mlat=Mlat+180;}
	//DiffSM= Sl-Ml;// ΑΝ Η ΔΙΑΦΟΡΑ ΤΗ ΓΩΝΙΑΣ ΤΟΥ ΗΛΙΟΥ ΚΑΙ ΤΗΣ ΣΕΛΗΝΗΣ ΕΙΝΑΙ ΜΕΓΑΛΥΤΕΡΗ ΑΠΟ 102 ΜΟΙΡΕΣ ΤΟΤΕ
	AbsDiff=Math.abs(Mlat-SlatW);
	sinf=Math.sin(torad(AbsDiff));
	cosf=(Logos+Math.cos(torad(AbsDiff)));
		W1=todeg(Math.atan2(sinf,cosf));
		if (Mlat>SlatW){Wl=Mlat-W1;};
		if (Mlat<SlatW){Wl=Mlat+W1;};
		//if (W1>90){W1=W1-180;};
return Wl;
}
//Βρισκουμε τις αλλαγες του ρευματος εντος της ημέρας
//Μετατροπή σε  Ιουλιανή ημερομηνία JD = Ιδιο με το Jdate
@SuppressWarnings("deprecation")
public  void changefinder( )
	{
	int Mi;
	double Mlog,Mlat,Slog,Slat,Alog,Alat = 0,Aslog = 0,Aslat,WLat = 0;
	double mlong,slong,Amlong,Df = 0,Wf1 = 0,AWf1 = 0;
	int f0,f1 = 0,f2 = 0,f3;
	MSdif[0]=0;
	MSdif[1]=0;
	MSdif[2]=0;
	MSdif[3]=0;
	MSdif[4]=0;
	MSdif[5]=0;
	MSdif[6]=0;
	MSdif[7]=0;
	//MSdif[8]=0;

	Mi=0;
	Date Dnext = DateNext;
	//Γυρνάμε την ημερομηνία 12 ώρες πίσω 
for (int i=1; i<=720;){Dnext.setMinutes(Dnext.getMinutes()-1);i++;}
	
//	Dnext.setSeconds(0);
//	Dnext.setHours(0);
//	Dnext.setMinutes(0);
	
	for (;;)
  {
		JDtime(Dnext);
		TtimeC(TJD); // ΒΡΙΣΚΟΥΜΕ ΤΟ Τ
		Azimouth();
		SunsLongitude(TJD);
		MoonRise[Mi]=Altitude();
	if (Mi<=1440){	DNext[Mi]=Dnext.getTime();};
		 mlong=MoonLogitude();
		 slong=SunEarthLong;
		 Amlong=AntimLong;
		 mlong=RoundTo2Decimals(mlong);
		 slong=RoundTo2Decimals(slong);
		 Amlong=RoundTo2Decimals(Amlong);
			Slat=(RoundTo2Decimals(SunDecl)); //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΗΛΙΟΥ
			Mlat=RoundTo2Decimals(MoonDecl);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ		
			Alat=RoundTo2Decimals(-MoonDecl);
		 Double DiffSaM1 = 0.00;
		 Double DiffSM1 = 0.00;


		 if (Amlong>0 && slong>0){DiffSaM1 = ((Amlong-slong));};
		 if (Amlong>0 && slong<0){DiffSaM1 = ((Amlong-(slong+360)));};	
		 if (Amlong<0 && slong>0){DiffSaM1 =((Amlong+360-slong));};
		 if (Amlong<0 && slong<0){DiffSaM1 = ((Amlong-slong));};
		 if (mlong>0 && slong>0){DiffSM1 =((mlong-slong));};
		 if (mlong>0 && slong<0){DiffSM1 = ((mlong-(slong+360)));};	
		 if (mlong<0 && slong>0){DiffSM1 = ((mlong+360-slong));};
		 if (mlong<0 && slong<0){DiffSM1 = ((mlong-slong));};
		 if (DiffSaM1>180){DiffSaM1=DiffSaM1-360;};
		 if (DiffSM1>180){DiffSM1=DiffSM1-360;};
		 if (DiffSaM1<-180){DiffSaM1=DiffSaM1+360;};
		 if (DiffSM1<-180){DiffSM1=DiffSM1+360;};	


			if (DiffSM1>-105 && 75>DiffSM1){Wf1=W2(slong,mlong);Df=RoundTo2Decimals(DiffSM1);WLat=WLat2(Slat,Mlat);
			f2=(int) (0.00002*Math.pow(DiffSM1,3)+0.0021*Math.pow(DiffSM1,2)-0.062*DiffSM1+256);
			f1=(int) (0.000009*Math.pow(DiffSM1,3)+0.0011*Math.pow(DiffSM1,2)-0.0406*DiffSM1+169);}
			
			if (DiffSM1<-105 ){Wf1=W2(slong,Amlong);Df=RoundTo2Decimals(DiffSaM1);WLat=WLat2(Slat,Alat);
			f2=(int) (0.00002*Math.pow(DiffSaM1,3)+0.0021*Math.pow(DiffSaM1,2)-0.062*DiffSaM1+256);
			f1=(int) (0.00003*Math.pow(DiffSaM1,3)+0.0025*Math.pow(DiffSaM1,2)-0.1395*DiffSaM1+166.13);}
			if (75<DiffSM1){Wf1=W2(slong,Amlong);Df=RoundTo2Decimals(DiffSaM1);WLat=WLat2(Slat,Alat);
			f2=(int) (0.00002*Math.pow(DiffSaM1,3)+0.0021*Math.pow(DiffSaM1,2)-0.062*DiffSaM1+256);
			f1=(int) (0.00003*Math.pow(DiffSaM1,3)+0.0025*Math.pow(DiffSaM1,2)-0.1395*DiffSaM1+166.13);}
			//y = 3E-05x3 + 0,0025x2 - 0,1395x + 166,13
			if ((180-Wf1)>0){AWf1=Wf1-180;}// f={80,172,255,352}; f={73,165,245,342};
			if ((180-Wf1)<0){AWf1=Wf1+180;}// f={100,8,-75,352};
			int c=(int)(RoundTo2Decimals(180.00-Wf1+LocalLong/2)); //βρίσκει το κέντρο ανάλογα με το W
			int c1=(int)RoundTo2Decimals(180.00-AWf1+LocalLong/2);//Βρίσκει το αντίθετo W
			int c2=((c+c1)/2)-180; //Χωρίζει τα W και το αντίθετό του στα 4
			int c3=((c+c1)/2);
			//Βρίσκουμε το έυρος των καμπυλών - εχει ρυθμιστεί 90 μοίρες 
		   	f0=f1-90;//f[0]+Math.abs((int)((Df*Logos/10)));//(Df*0.222222));
		  	//f1=f[1]+Math.abs((int)((Df*Logos/10)));//(Df*0.222222));
		  	//f2=f[2]+Math.abs((int)((Df*Logos/10)));//(Df*0.222222));
		  	f3=f2+90;//f[3]+Math.abs((int)((Df*Logos/10)));//(Df*0.222222));
			int AmWidth=((f2-f1)/2);
			int mWidth= ((f0-(f3-360))/2);
			int Am90=((f1-f0)/2);
			int Am180= ((f3-f2)/2);
			//Θέτωντας το C (W) σαν το κέντρο των μετρήσεων βρίσκουμε 
			//πότε οι αλλαγές περνουν από το Γεωγρ. μήκος της Χαλκίδος (Αφού c το κέντρο της καμπύλης και πχ AmWidth 
			//το μισό της καμύλης) ουσιαστικά βρίσκουμε πότε W περνάει από το σημείο
			if (Math.round(c-AmWidth)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[3]=DNext[Mi];}}//00
			if (Math.round(c1-mWidth)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[7]=DNext[Mi];}}//11
			if (Math.round(c3-Am180)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[5]=DNext[Mi];}}//22
			if (Math.round(c2-Am90)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[1]=DNext[Mi];}}//33
			
			if (Math.round(c+AmWidth)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[4]=DNext[Mi];}}//000
			if (Math.round(c1+mWidth)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[0]=DNext[Mi];}}//111
			if (Math.round(c3+Am180)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[6]=DNext[Mi];}}//222
			if (Math.round(c2+Am90)==Math.round(180-LocalLong)){if (Mi<=1440){MSdif[2]=DNext[Mi];}}//333
			//if (Mi<=1440){MSdif[8]=DNext[Mi];}
		SunRise[Mi]=SunAlt;

		Mi++;
		Dnext.setMinutes(Dnext.getMinutes()+1);
		
		if (Mi>=1440) break;}
	MaxW=WLat;
	
	}
//Βρισκουμε τις αλλαγες του ρευματος εντος της ημέρας
//Μετατροπή σε  Ιουλιανή ημερομηνία JD = Ιδιο με το Jdate
@SuppressWarnings("deprecation")
/**
public  void changefinder2( )
	{
	int Mi;
	double Amlong,Df = 0;
	double[] Wf1=new double[1440];
	double[] AWf1=new double[1440];
	double[] mlong=new double[1440];
	double[] slong=new double[1440];
	double[] mlat=new double[1440];
	double[] slat=new double[1440];
	double[] fm0=new double[1440];
	double[] fm1=new double[1440];
	double[] fm2=new double[1440];
	double[] fm3=new double[1440];

	double[] Date1=new double[4];

	int flag3=0,flag2=0,flag1=0,flag0=0,flag31;
	for (int i=0;i<4;){
		Date0[i]=0;
		Date1[i]=0;
		fw[i]=0;
		MSdif[i]=0;
		i++;
	};

	
	//MSdif[8]=0;
	MaxW=0;
	Mi=0;
	Date Dnext = DateNext;
	//Γυρνάμε την ημερομηνία 12 ώρες πίσω 
for (int i=1; i<=720;){Dnext.setMinutes(Dnext.getMinutes()-1);i++;}
	
//	Dnext.setSeconds(0);
//	Dnext.setHours(0);
//	Dnext.setMinutes(0);

	for (;;)
  {
		JDtime(Dnext);
		TtimeC(TJD); // ΒΡΙΣΚΟΥΜΕ ΤΟ Τ
		Azimouth();
		SunsLongitude(TJD);
		MoonRise[Mi]=Altitude();
	if (Mi<=1440){	DNext[Mi]=Dnext.getTime();};
		 mlong[Mi]=RoundTo2Decimals(MoonLogitude());
		 slong[Mi]=RoundTo2Decimals(SunEarthLong);
		 Amlong=RoundTo2Decimals(AntimoonLong(mlong[Mi]));
			slat[Mi]=RoundTo2Decimals(SunDecl); //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΗΛΙΟΥ
			mlat[Mi]=RoundTo2Decimals(MoonDecl);
		 Double DiffSaM1 = 0.00;
		 Double DiffSM1 = 0.00;
		 if (mlong[Mi]>0 && slong[Mi]>0){DiffSM1 =((mlong[Mi]-slong[Mi]));};
		 if (mlong[Mi]>0 && slong[Mi]<0){DiffSM1 = ((mlong[Mi]-(slong[Mi]+360)));};	
		 if (mlong[Mi]<0 && slong[Mi]>0){DiffSM1 = ((mlong[Mi]+360-slong[Mi]));};
		 if (mlong[Mi]<0 && slong[Mi]<0){DiffSM1 = ((mlong[Mi]-slong[Mi]));};
		 if (DiffSM1>180){DiffSM1=DiffSM1-360;};
		 if (DiffSM1<-180){DiffSM1=DiffSM1+360;};	
		 
if (DiffSM1>-105 && 75>DiffSM1){
			 Wf1[Mi]=W2(slong[Mi],mlong[Mi]);	//παίρνουμε το W
			 double Wf180 = 0;
			 if (Wf1[Mi]>0){AWf1[Mi]=Wf1[Mi]-180;};
			 if (Wf1[Mi]==0){AWf1[Mi]=180;};
			 if (Wf1[Mi]<0){AWf1[Mi]=Wf1[Mi]+180;};//παίρνουμε τον αντίποδα του W
			// if ( Math.abs(Wf1[Mi])>Math.abs(Wf180)){Wf1[Mi]=Wf180;}; //Κρατάμε αυτό που ΕΙΝΑΙ μεταξύ 90 και -90 περίπου
			 //και μετστρέπουμε όλα τα W μεταξύ 90 και -90 
			 double W360=180-Wf1[Mi]; //μετατρέπουμε το W σε 0-360 μοιρες περίπου	
			 double AW360=180-AWf1[Mi];//μετατρέπουμε το AW σε 0-360 μοιρες περίπου	
			 double D3=Math.pow(DiffSM1, 3); //η τρίτη δύναμη της γωνίας 
			 double D2=Math.pow(DiffSM1, 2); //η δεύτερη δύναμη της γωνίας

			 fm2[Mi]= 0.00002*D3 + 0.0021*D2 - 0.062*DiffSM1 + 256;
			 fm1[Mi]= 0.000009*D3 + 0.0011*D2 - 0.0406*DiffSM1 + 170;
			 fm3[Mi]= 0.00002*D3 + 0.0021*D2 - 0.062*DiffSM1 + 256;
			 fm0[Mi]= 0.000009*D3 + 0.0011*D2 - 0.0406*DiffSM1 + 170;
				
			  if (272>=W360 && W360>256 && flag2<1){if (W360<fm2[Mi]){Date0[2]=DNext[Mi];MSdif[2]=W360;flag2=1;}} //W μεγαλύτερο από ~353,26
			  if (177>=W360 && W360>170 && flag1<1){if (W360<fm1[Mi]){Date0[1]=DNext[Mi];MSdif[1]=W360;flag1=1;}} //W μεγαλύτερο από ~353,26
			  if (272>=AW360 && AW360>256 && flag3<1){if (AW360<fm3[Mi]){Date0[3]=DNext[Mi];MSdif[3]=AW360;flag3=1;}} //W μεγαλύτερο από ~353,26
			 if (177>=AW360 && AW360>170 && flag0<1){if (AW360<fm0[Mi]){Date0[0]=DNext[Mi];MSdif[0]=AW360;flag0=1;}} //W μεγαλύτερο από ~353,26

}//Df=RoundTo2Decimals(DiffSM1);}
	//___________________________________________________________________________________________________________________________		 
				 if (Amlong>0 && slong[Mi]>0){DiffSaM1 = ((Amlong-slong[Mi]));};
				 if (Amlong>0 && slong[Mi]<0){DiffSaM1 = ((Amlong-(slong[Mi]+360)));};	
				 if (Amlong<0 && slong[Mi]>0){DiffSaM1 =((Amlong+360-slong[Mi]));};
				 if (Amlong<0 && slong[Mi]<0){DiffSaM1 = ((Amlong-slong[Mi]));};
				 if (DiffSaM1>180){DiffSaM1=DiffSaM1-360;};		 
				 if (DiffSaM1<-180){DiffSaM1=DiffSaM1+360;};
				 
if (DiffSM1<=-105 || 75<=DiffSM1){
	Wf1[Mi]=W2(slong[Mi],Amlong);

										//για anti 2E-05x3 + 0,0022x2 - 0,0768x + 255,58

										//		  = 3E-05x3 + 0,0025x2 - 0,1395x + 166,13

	 double Wf180 = 0;
	 if (Wf1[Mi]>0){AWf1[Mi]=Wf1[Mi]-180;};
	 if (Wf1[Mi]==0){AWf1[Mi]=180;};
	 if (Wf1[Mi]<0){AWf1[Mi]=Wf1[Mi]+180;};//παίρνουμε τον αντίποδα του W
	// if ( Math.abs(Wf1[Mi])>Math.abs(Wf180)){Wf1[Mi]=Wf180;}; //Κρατάμε αυτό που ΕΙΝΑΙ μεταξύ 90 και -90 περίπου
	 //και μετστρέπουμε όλα τα W μεταξύ 90 και -90 
	 double W360=180-Wf1[Mi]; //μετατρέπουμε το W σε 0-360 μοιρες περίπου	
	 double AW360=180-AWf1[Mi];//μετατρέπουμε το AW σε 0-360 μοιρες περίπου	
	 double D3=Math.pow(DiffSaM1, 3); //η τρίτη δύναμη της γωνίας 
	 double D2=Math.pow(DiffSaM1, 2); //η δεύτερη δύναμη της γωνίας

	 fm2[Mi]= 0.00002*D3 + 0.0022*D2 - 0.0768*DiffSaM1 + 255.58;
	 fm1[Mi]= 0.00003*D3 + 0.0025*D2+ 0.1395*DiffSaM1 + 166.13;
	 fm3[Mi]= 0.00002*D3 + 0.0022*D2 - 0.0768*DiffSaM1 + 255.58;
	 fm0[Mi]= 0.00003*D3 + 0.0025*D2+ 0.1395*DiffSaM1 + 166.13;
		
	  if (271>=W360 && W360>255 && flag2<1){if (W360<fm2[Mi]){Date0[2]=DNext[Mi];MSdif[2]=W360;flag2=1;}} //W μεγαλύτερο από ~353,26
	  if (183>=W360 && W360>165 && flag1<1){if (W360<fm1[Mi]){Date0[1]=DNext[Mi];MSdif[1]=W360;flag1=1;}} //W μεγαλύτερο από ~353,26
	  if (271>=AW360 && AW360>255 && flag3<1){if (AW360<fm3[Mi]){Date0[3]=DNext[Mi];MSdif[3]=AW360;flag3=1;}} //W μεγαλύτερο από ~353,26
	  if (183>=AW360 && AW360>165 && flag0<1){if (AW360<fm0[Mi]){Date0[0]=DNext[Mi];MSdif[0]=AW360;flag0=1;}} //W μεγαλύτερο από ~353,26
	
}
		 SunRise[Mi]=SunAlt;
		if (MaxW<180.00-Wf1[720]){MaxW=180.00-Wf1[720];}
		Mi++;
		Dnext.setMinutes(Dnext.getMinutes()+1);
		if (Mi>=1440) {break;}

		  }
//	int j=0;
	//int k=0;
	//for (j=0;j<4;){
	//for (int i=0;i<4;){
	//	if (Date0[j]<Date1[i]){Date0[j]=Date1[i]; k=i;};
	//	if (fw[j]<MSdif[i]){fw[j]=MSdif[i]; k=i;};
	//	i++;
	//};
	//Date1[k]=0;
	//MSdif[k]=0;
	//j++;
	//};
	}
*/
//ROUND A DOUBLE WITH TWO DECIMAL
double RoundTo2Decimals(double val){
	double f=Math.round(val*100);
	return f/100;
}
/*public  void Graphs( ){
	double Mlog,Mlat,Slog,Slat,Alog,Alat,Aslog = 0,Aslat;
	int f0,f1,f2,f3;
	float Mlog2 ,Mlat2,Slog2,Slat2,Alog2,Alat2,Aslat2,Aslog2,Wlong,Wlat,AWlong = 0,AWlat;
	c.set(year, month, day, hour, minute);
	DateNext=  c.getTime();
	JDtime(DateNext);
	TtimeC(TJD);
	Azimouth();
	SunsLongitude(TJD);
	Altitude();
	SimpleDateFormat Datenow = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	String formattedDatenow = Datenow.format(DateNext);

	Slog=SunEarthLong; //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΗΛΙΟΥ
	Mlog=MoonLogitude();
	Alog=AntimLong;
	
	if (Slog>0) {Aslog=Slog-180;}
	if (Slog<0) {Aslog=Slog+180;}
	if (Slog==0) {Aslog=180;}
	Slog=RoundTo2Decimals(Slog);
	Aslog=RoundTo2Decimals(Aslog);
	Mlog=RoundTo2Decimals(Mlog);			//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ 
	Alog=RoundTo2Decimals(Alog);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΑΝΤΙΠΟΔΑ ΤΗΣ ΣΕΛΗΝΗΣ 
	Slat=(RoundTo2Decimals(SunDecl)); //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΗΛΙΟΥ
	Mlat=RoundTo2Decimals(MoonDecl);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ		
	Alat=RoundTo2Decimals(-MoonDecl);
	double Ds = 0;
	Katastasi=" ";
	 if (Alog>0 && Slog>0){DiffSaM = ((Alog-Slog));};
	 if (Alog>0 && Slog<0){DiffSaM = ((Alog-(Slog+360)));};	
	 if (Alog<0 && Slog>0){DiffSaM =((Alog+360-Slog));};
	 if (Alog<0 && Slog<0){DiffSaM = ((Alog-Slog));};
	 if (Mlog>0 && Slog>0){DiffSM =((Mlog-Slog));};
	 if (Mlog>0 && Slog<0){DiffSM = ((Mlog-(Slog+360)));};	
	 if (Mlog<0 && Slog>0){DiffSM = ((Mlog+360-Slog));};
	 if (Mlog<0 && Slog<0){DiffSM = ((Mlog-Slog));};
	 if (DiffSaM>180){DiffSaM=DiffSaM-360;};
	 if (DiffSM>180){DiffSM=DiffSM-360;};
	 if (DiffSaM<-180){DiffSaM=DiffSaM+360;};
	 if (DiffSM<-180){DiffSM=DiffSM+360;};
	 String M="-";
	 	if (DiffSM>-105 && DiffSM<0){Wf=W2(Slog,Mlog);Ds=RoundTo2Decimals(DiffSM);M="M";Wl=WLat2(Slat,Mlat);}
	 	if (DiffSM>=0 && 75>DiffSM){Wf=W2(Slog,Mlog);Ds=RoundTo2Decimals(DiffSM);M="M";Wl=WLat2(Slat,Mlat);}
	 	if (DiffSM<=-105 ){Wf=W2(Slog,Alog);Ds=RoundTo2Decimals(DiffSaM);M="A";Wl=WLat2(Slat,Alat);}
		if (75<=DiffSM){Wf=W2(Slog,Alog);Ds=RoundTo2Decimals(DiffSaM);M="A";Wl=WLat2(Slat,Alat);}
	 	if (DiffSM<105 && DiffSM>75){Katastasi="Περίοδος ακαταστασίας";}//SOS όταν περνάει ο ήλιος από το πρώτο τέταρτο η γωνία πρέπει να είναι 75 πχ 4/11/15 στις 20:21 ηταν από νότο η φορά 
	 	if (DiffSM>-105 && DiffSM<-75){Katastasi="Περίοδος ακαταστασίας";}//άλλα στον πίνακα της παλλίροιας φαίνεται σωστά - στις 22:25 αργά από βορρά
AngleSM=0;
AngleSM=RoundTo2Decimals(DiffSM);
double D3=Math.pow(Ds, 3); //η τρίτη δύναμη της γωνίας 
double D2=Math.pow(Ds, 2); //η δεύτερη δύναμη της γωνίας

 	f0=f[0]+Math.abs((int)(Ds*Logos/10));//0.00002*D3 + 0.0022*D2 - 0.0768*DiffSaM1 + 255.58;
	f1=f[1]+Math.abs((int)(Ds*Logos/10));// 0.00003*D3 + 0.0025*D2+ 0.1395*DiffSaM1 + 166.13;
	f2=f[2]+Math.abs((int)(Ds*Logos/10));//(Ds*0.222222));
	f3=f[3]+Math.abs((int)(Ds*Logos/10));//(Ds*0.222222));
f0=(int) fw[0];
f1=(int) fw[1];
f2=(int) fw[2];
f3=(int) fw[3];
	//if (Wf>360){Wf=Wf-360;}}
	//if (DiffSM>DiffSaM) {Wf=W2(Slog,Alog);}//;if (Wf>360){Wf=Wf-360;}}
		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΑΝΤΙΠΟΔΑ ΤΗΣ ΣΕΛΗΝΗΣ 
		Aslat=RoundTo2Decimals(-Slat);
				StrSlong ="-";
				StrSlat ="-";
				StrMlat ="-";
				StrMlog ="-";
						StrMlog=Mlog+" Δ";
		if (Mlog<0){	StrMlog=-Mlog+" Α";}
						StrSlong=Slog+" Δ";
		if (Slog<0){	StrSlong=-Slog+" Α";}			
						StrMlat=Mlat+" B";
		if (Mlat<0){	StrMlat=-Mlat+" N";}		
						StrSlat=Slat+" B";
		if (Slat<0){	StrSlat=-Slat+" N";}		
		Slog2=(float) ((180.00-Slog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΗΛΙΟΥ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Slat2=(float) ((BitmapHeight/2)-(Slat)*(BitmapHeight/130));	//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΗΛΙΟΥ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Mlog2=(float) ((180.00-Mlog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Mlat2=(float) ((BitmapHeight/2)-(Mlat)*(BitmapHeight/130));	//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Alog2=(float) ((180.00-Alog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Alat2=(float) ((BitmapHeight/2)-(Alat)*(BitmapHeight/130));	//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Aslog2=(float) ((180.00-Aslog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Aslat2=(float) ((BitmapHeight/2)-(Aslat)*(BitmapHeight/130));
		Wlong=(float) ((180.00-Wf)*BitmapWidth/360);
		Wlat=(float) ((BitmapHeight/2)-(Wl)*(BitmapHeight/130));
		if (Wf>0){AWlong=(float) ((180.00-(Wf-180))*BitmapWidth/360);};
		if (Wf<0){AWlong=(float) ((180.00-(Wf+180))*BitmapWidth/360);};
		AWlat=(float) ((BitmapHeight/2)-(-Wl)*(BitmapHeight/130));
		//Wlog2=(float) (((Wf))*(BitmapWidth/360));
		//changefinder( );		
		//ΜΕΤΑΤΡΟΠΗ ΤΩΝ ΣΥΝΤΕΤΑΓΜΕΝΩΝ ΣΕ STRING
		canvas.drawLine(90*BitmapWidth/360, 0,90*BitmapWidth/360,(float)(BitmapHeight-BitmapHeight*0.25), pBlack);
		canvas.drawLine(180*BitmapWidth/360, 0,180*BitmapWidth/360,(float)(BitmapHeight-BitmapHeight*0.25), pBlack);
		canvas.drawLine(270*BitmapWidth/360, 0,270*BitmapWidth/360,(float)(BitmapHeight-BitmapHeight*0.25), pBlack);
		canvas.drawLine(0,BitmapHeight/2,BitmapWidth,BitmapHeight/2, pBlack);
		//canvas.drawLine(0,(float)((BitmapHeight/2)-(23.44)*(BitmapHeight/130)),BitmapWidth,(float)((BitmapHeight/2)-(23.44)*(BitmapHeight/130)), pBlack);
		//canvas.drawLine(0,(float)((BitmapHeight/2)+(23.44)*(BitmapHeight/130)),BitmapWidth,(float)((BitmapHeight/2)+(23.44)*(BitmapHeight/130)), pBlack);

		//SimpleDateFormat yearf = new SimpleDateFormat("dd/MM HH:mm");
		SimpleDateFormat yearf = new SimpleDateFormat("HH:mm");
		String MoonRiseS = "-";
		String MoonSetS = "-";
		if (iMrise>0 && iMrise<=1440){SetRiseTime.setTime(DNext[iMrise]);}
		MoonRiseS = yearf.format(SetRiseTime);
		if (iMrise==1440){ MoonRiseS = "OXI";}	
		if (iMset>0 && iMset<=1440){SetRiseTime.setTime(DNext[iMset]);}
		MoonSetS= yearf.format(SetRiseTime);
		if (iMset==1440){MoonSetS= "OXI";}
		if (iSrise>0 && iSrise<=1440){SetRiseTime.setTime(DNext[iSrise]);}
		String SunRiseS = yearf.format(SetRiseTime);
		if (iSset>0 && iSset<=1440){SetRiseTime.setTime(DNext[iSset]);}
		String SunSetS= yearf.format(SetRiseTime);	
		FText.setText(formattedDatenow+" Σελήνη "+MoonDays+","+ (MoonHrs*100/24+MoonMins*100/1440)+ " ημερών ");  
	canvas.drawCircle(Mlog2, Mlat2, 50, pWhite); 				//ΑΠΟΤΥΠΩΣΗ ΤΗΣ ΣΕΛΗΝΗΣ ΣΤΟ BITMAP
	canvas.drawCircle(Slog2, Slat2, 50, pYellow);				//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΗΛΙΟΥ ΣΤΟ BITMAP
	canvas.drawCircle(Alog2, Alat2, 50, pBlack);	
	canvas.drawCircle(Aslog2, Aslat2, 50, pBlack);//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP
	canvas.drawCircle(Wlong, Wlat, 50, pOrange);//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP
	canvas.drawCircle(AWlong, AWlat, 50, pOrange);//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP
//βρίσκουμε την μέση τιμή των μηδενισμων των καμύλων
	double[] Chang=new double[5];
	Chang[0]=0;
	Chang[1]=0;
	Chang[2]=0;
	Chang[3]=0;
	String ch0="-";
	String ch1="-";
	String ch2="-";
	String ch3="-";
	if (MSdif[0]>0 && MSdif[1]>0){ Chang[0]=(MSdif[0]+MSdif[1])/2;};
	if (MSdif[0]==0){ Chang[0]=MSdif[1];};if (MSdif[1]==0){ Chang[0]=MSdif[0];};
	
	if (MSdif[2]>0 && MSdif[3]>0){ Chang[1]=(MSdif[2]+MSdif[3])/2;};
	if (MSdif[2]==0){ Chang[1]=MSdif[3];};if (MSdif[3]==0){ Chang[1]=MSdif[2];};
	
	if (MSdif[4]>0 && MSdif[5]>0){ Chang[2]=(MSdif[4]+MSdif[5])/2;};
	if (MSdif[4]==0){ Chang[2]=MSdif[5];};if (MSdif[5]==0){ Chang[2]=MSdif[4];};
	
	if (MSdif[6]>0 && MSdif[7]>0){ Chang[3]=(MSdif[6]+MSdif[7])/2;};
	if (MSdif[6]==0){ Chang[3]=MSdif[7];};if (MSdif[7]==0){ Chang[3]=MSdif[6];};
	
	if (Chang[0]>0 ){ ch0 =yearf.format(Chang[0]);};
	if (Chang[1]>0 ){ ch1 =yearf.format(Chang[1]);};
	if (Chang[2]>0 ){ch2 =yearf.format(Chang[2]);};
	if (Chang[3]>0 ){ ch3 =yearf.format(Chang[3]);};

	//Σχηματιζουμε την καμπύλη ροής
	//if (Wf>360){Wf=Wf-360;}
	if ((180-Wf)>0){AWf=Wf-180;}// f={80,172,255,352}; f={73,165,245,342};
	if ((180-Wf)<0){AWf=Wf+180;}// f={100,8,-75,352};
	int c=(int)(RoundTo2Decimals(180.00-Wf+LocalLong/2));
	int c1=(int)RoundTo2Decimals(180.00-AWf+LocalLong/2);
	int c2=((c+c1)/2)-180;
	int c3=((c+c1)/2);
	float LineHeight=(float)(BitmapHeight-BitmapHeight*0.90);
	int AmWidth=((f2-f1)/2);
	int mWidth= ((f0-(f3-360))/2);
	int Am90=((f1-f0)/2);
	int Am180= ((f3-f2)/2);
	int ig; 
	SetL=0;
//σχηματισμός καμπύλης Wf   RED Θετικές τιμές GREEN Αρνητικές τιμές
	int ki=0;
	for (ig=c-AmWidth; ig<c+AmWidth; ){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight+LineHeight*(float) Math.sin(torad((ig-(c+AmWidth))*180/(AmWidth*2))), pRed);
	if (ki==Math.round(180-LocalLong)){SetL=-(float) Math.sin(torad((ig-(c+AmWidth))*180/(AmWidth*2)));}
	ig++;}			
	canvas.drawText(ch2,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);
//σχηματισμός καμπύλης Wf+90
	for (ig=c3-Am180; ig<=c3+Am180;){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight-LineHeight*(float) Math.sin(torad((ig-(c3+Am180))*180/(Am180*2))), pGreen);
	if (ki==Math.round(180-LocalLong)){SetL=(float) Math.sin(torad((ig-(c3+Am180))*180/(Am180*2)));}
	ig++;}
	canvas.drawText(ch3,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);
//σχηματισμός καμπύλης Wf-90
	for (ig=c2-Am90; ig<=c2+Am90;){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight-LineHeight*(float) Math.sin(torad((ig-(c2+Am90))*180/(Am90*2))), pGreen);
	if (ki==Math.round(180-LocalLong)){SetL=(float) Math.sin(torad((ig-(c2+Am90))*180/(Am90*2)));}
	ig++;}
	canvas.drawText(ch1,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);
//σχηματισμός καμπύλης AWf
	for (ig=c1-mWidth; ig<c1+mWidth;){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight+LineHeight*(float) Math.sin(torad((ig-(c1+mWidth))*180/(mWidth*2))), pRed);
	if (ki==Math.round(180-LocalLong)){SetL=-(float) Math.sin(torad((ig-(c1+mWidth))*180/(mWidth*2)));}
	ig++;}
	canvas.drawText(ch0,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);

	String A00="-";
	String A11="-";
	String A22="-";
	String A33="-";
	String A000="-";
	String A111="-";
	String A222="-";
	String A333="-";
	String ATest="-";
	if (Chang[0]>0 ){ A00 =yearf.format(Chang[0]-1000000);
					  A000 =yearf.format(Chang[0]+1000000);};
	if (Chang[1]>0 ){ A11 =yearf.format(Chang[1]-1000000);
					  A111 =yearf.format(Chang[1]+1000000);};
	if (Chang[2]>0 ){ A22 =yearf.format(Chang[2]-1000000);
					  A222 =yearf.format(Chang[2]+1000000);};
	if (Chang[3]>0 ){ A33 =yearf.format(Chang[3]-1000000);
					  A333 =yearf.format(Chang[3]+1000000);};


	String f1TS = ("("+A00+"-"+A000+"), ("+A11+"-"+A111+"), ("+A22+"-"+A222+"), ("+A33+"-"+A333+")");
	canvas.drawLine((float)(180-LocalLong)*BitmapWidth/360,0,(float) (180-LocalLong)*BitmapWidth/360,(float)(BitmapHeight*0.18), pYellow);
	if(SetL>0.14){fora="Φορά από νότο με ταχύτητα "+RoundTo2Decimals(SetL*100)+" %";}
	if(SetL<-0.14){fora="Φορά από βορρά με ταχύτητα "+RoundTo2Decimals(-SetL*100)+" %";}
	if(SetL<0.14 && SetL>-0.14){fora="Φάση Αλλαγής Ρεύματος -  Ταχύτητα χαμηλή";}
	F2Text.setText(fora);//+" W: "+RoundTo2Decimals(Wf)+" "+M+" A: "+RoundTo2Decimals(DiffSM));
	//if (MSdif[8]>0 ){ ATest =yearf.format(MSdif[8]);}
	//F2Text.setText(" W: "+RoundTo2Decimals(Wf)+" "+M+" A: "+RoundTo2Decimals(DiffSM)+" MaxW: "+RoundTo2Decimals(MaxW)+" "+ATest);
	canvas.drawText(f1TS,0,(float)(BitmapHeight-BitmapHeight*0.02), pYellow);
	canvas.drawText("Προβλεπόμενες αλλαγές ρεύματος :"+Katastasi,0,(float)(BitmapHeight-BitmapHeight*0.12), pYellow);
	canvas.drawText("Ήλιος : "+StrSlong+" - "+StrSlat+"    Σελήνη : "+StrMlog+" - "+StrMlat,0,(float)(BitmapHeight-BitmapHeight*0.21), pWhite);
}*/
public  void Graphs( ){
	double Mlog,Mlat,Slog,Slat,Alog,Alat = 0,Aslog = 0,Aslat;
	int f0,f1 = 0,f2=0,f3;
	float Mlog2 ,Mlat2,Slog2,Slat2,Alog2,Alat2,Aslat2,Aslog2,Wlong,Wlat,AWlong = 0,AWlat;
	c.set(year, month, day, hour, minute);
	DateNext=  c.getTime();
	JDtime(DateNext);
	TtimeC(TJD);
	Azimouth();
	SunsLongitude(TJD);
	Altitude();
	SimpleDateFormat Datenow = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	String formattedDatenow = Datenow.format(DateNext);
	Slog=SunEarthLong; //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΗΛΙΟΥ
	Mlog=MoonLogitude();
	Alog=AntimLong;
	
	if (Slog>0) {Aslog=Slog-180;}
	if (Slog<0) {Aslog=Slog+180;}
	if (Slog==0) {Aslog=180;}

	Slog=SunEarthLong; //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΗΛΙΟΥ
	Mlog=MoonLogitude();
	Alog=AntimLong;
	Slog=RoundTo2Decimals(Slog);
	Mlog=RoundTo2Decimals(Mlog);			//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ 
	Alog=RoundTo2Decimals(Alog);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΑΝΤΙΠΟΔΑ ΤΗΣ ΣΕΛΗΝΗΣ 
	Slat=(RoundTo2Decimals(SunDecl)); //ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΗΛΙΟΥ
	Mlat=RoundTo2Decimals(MoonDecl);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ		
	Alat=RoundTo2Decimals(-MoonDecl);
	double Ds = 0;
	Katastasi=" ";
	 if (Alog>0 && Slog>0){DiffSaM = ((Alog-Slog));};
	 if (Alog>0 && Slog<0){DiffSaM = ((Alog-(Slog+360)));};	
	 if (Alog<0 && Slog>0){DiffSaM =((Alog+360-Slog));};
	 if (Alog<0 && Slog<0){DiffSaM = ((Alog-Slog));};
	 if (Mlog>0 && Slog>0){DiffSM =((Mlog-Slog));};
	 if (Mlog>0 && Slog<0){DiffSM = ((Mlog-(Slog+360)));};	
	 if (Mlog<0 && Slog>0){DiffSM = ((Mlog+360-Slog));};
	 if (Mlog<0 && Slog<0){DiffSM = ((Mlog-Slog));};
	 if (DiffSaM>180){DiffSaM=DiffSaM-360;};
	 if (DiffSM>180){DiffSM=DiffSM-360;};
	 if (DiffSaM<-180){DiffSaM=DiffSaM+360;};
	 if (DiffSM<-180){DiffSM=DiffSM+360;};
	 String M="-";
		/*if (DiffSM1>-105 && 75>DiffSM1){Wf1=W2(slong,mlong);Df=RoundTo2Decimals(DiffSM1);WLat=WLat2(Slat,Mlat);
		f2=(int) (0.00002*Math.pow(DiffSM1,3)+0.0021*Math.pow(DiffSM1,2)-0.062*DiffSM1+256);
		f1=(int) (0.000009*Math.pow(DiffSM1,3)+0.0011*Math.pow(DiffSM1,2)-0.0406*DiffSM1+169);}
		if (DiffSM1<-105 ){Wf1=W2(slong,Amlong);Df=RoundTo2Decimals(DiffSaM1);WLat=WLat2(Slat,Alat);}
		if (75<DiffSM1){Wf1=W2(slong,Amlong);Df=RoundTo2Decimals(DiffSaM1);WLat=WLat2(Slat,Alat);}*/
	 	if (DiffSM>-105 && DiffSM<0){Wf=W2(Slog,Mlog);Ds=RoundTo2Decimals(DiffSM);M="M";Wl=WLat2(Slat,Mlat);
		f2=(int) (0.00002*Math.pow(DiffSM,3)+0.0021*Math.pow(DiffSM,2)-0.062*DiffSM+256);
		f1=(int) (0.000009*Math.pow(DiffSM,3)+0.0011*Math.pow(DiffSM,2)-0.0406*DiffSM+169);}
	 	if (DiffSM>=0 && 75>DiffSM){Wf=W2(Slog,Mlog);Ds=RoundTo2Decimals(DiffSM);M="M";Wl=WLat2(Slat,Mlat);
		f2=(int) (0.00002*Math.pow(DiffSM,3)+0.0021*Math.pow(DiffSM,2)-0.062*DiffSM+256);
		f1=(int) (0.000009*Math.pow(DiffSM,3)+0.0011*Math.pow(DiffSM,2)-0.0406*DiffSM+169);}
	 	if (DiffSM<=-105 ){Wf=W2(Slog,Alog);Ds=RoundTo2Decimals(DiffSaM);M="A";Wl=WLat2(Slat,Alat);
	 	f2=(int) (0.00002*Math.pow(DiffSaM,3)+0.0021*Math.pow(DiffSaM,2)-0.062*DiffSaM+256);
		f1=(int) (0.00003*Math.pow(DiffSaM,3)+0.0025*Math.pow(DiffSaM,2)-0.1395*DiffSaM+166.13);}
		if (75<=DiffSM){Wf=W2(Slog,Alog);Ds=RoundTo2Decimals(DiffSaM);M="A";Wl=WLat2(Slat,Alat);
	 	f2=(int) (0.00002*Math.pow(DiffSaM,3)+0.0021*Math.pow(DiffSaM,2)-0.062*DiffSaM+256);
		f1=(int) (0.00003*Math.pow(DiffSaM,3)+0.0025*Math.pow(DiffSaM,2)-0.1395*DiffSaM+166.13);}
	 	if (DiffSM<105 && DiffSM>75){Katastasi="Περίοδος ακαταστασίας";}//SOS όταν περνάει ο ήλιος από το πρώτο τέταρτο η γωνία πρέπει να είναι 75 πχ 4/11/15 στις 20:21 ηταν από νότο η φορά 
	 	if (DiffSM>-105 && DiffSM<-75){Katastasi="Περίοδος ακαταστασίας";}//άλλα στον πίνακα της παλλίροιας φαίνεται σωστά - στις 22:25 αργά από βορρά
AngleSM=0;
AngleSM=RoundTo2Decimals(DiffSM);
 	f0=f1-90;//f[0]+Math.abs((int)(Ds*Logos/10));//(Ds*0.222222));
	//f1=f[1]+Math.abs((int)(Ds*Logos/10));//(Ds*0.222222));
	//f2=f[2]+Math.abs((int)(Ds*Logos/10));//(Ds*0.222222));
	f3=f2+90;//f[3]+Math.abs((int)(Ds*Logos/10));//(Ds*0.222222));
	//if (Wf>360){Wf=Wf-360;}}
	//if (DiffSM>DiffSaM) {Wf=W2(Slog,Alog);}//;if (Wf>360){Wf=Wf-360;}}
		Mlat=RoundTo2Decimals(MoonDecl);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ		
		Alat=RoundTo2Decimals(-MoonDecl);		//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΑΝΤΙΠΟΔΑ ΤΗΣ ΣΕΛΗΝΗΣ 
		Aslat=RoundTo2Decimals(-Slat);
				StrSlong ="-";
				StrSlat ="-";
				StrMlat ="-";
				StrMlog ="-";
						StrMlog=Mlog+" Δ";
		if (Mlog<0){	StrMlog=-Mlog+" Α";}
						StrSlong=Slog+" Δ";
		if (Slog<0){	StrSlong=-Slog+" Α";}			
						StrMlat=Mlat+" B";
		if (Mlat<0){	StrMlat=-Mlat+" N";}		
						StrSlat=Slat+" B";
		if (Slat<0){	StrSlat=-Slat+" N";}		
		Slog2=(float) ((180.00-Slog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤΟΥ ΗΛΙΟΥ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Slat2=(float) ((BitmapHeight/2)-(Slat)*(BitmapHeight/130));	//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΟΥ ΗΛΙΟΥ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Mlog2=(float) ((180.00-Mlog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Mlat2=(float) ((BitmapHeight/2)-(Mlat)*(BitmapHeight/130));	//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Alog2=(float) ((180.00-Alog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Alat2=(float) ((BitmapHeight/2)-(Alat)*(BitmapHeight/130));	//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΠΛΑΤΟΣ ΤΗΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Aslog2=(float) ((180.00-Aslog)*BitmapWidth/360);				//ΤΟ ΓΕΩΓΡΑΦΙΚΟ ΜΗΚΟΣ ΤHΣ ΣΕΛΗΝΗΣ ΑΠΟ 0-360 ΣΕ ΑΝΑΛΟΓΙΑ ΤΟΥ BITMAP
		Aslat2=(float) ((BitmapHeight/2)-(Aslat)*(BitmapHeight/130));
		Wlong=(float) ((180.00-Wf)*BitmapWidth/360);
		Wlat=(float) ((BitmapHeight/2)-(Wl)*(BitmapHeight/130));
		if (Wf>0){AWlong=(float) ((180.00-(Wf-180))*BitmapWidth/360);};
		if (Wf<0){AWlong=(float) ((180.00-(Wf+180))*BitmapWidth/360);};
		AWlat=(float) ((BitmapHeight/2)-(-Wl)*(BitmapHeight/130));
		//Wlog2=(float) (((Wf))*(BitmapWidth/360));
		//changefinder( );		
		//ΜΕΤΑΤΡΟΠΗ ΤΩΝ ΣΥΝΤΕΤΑΓΜΕΝΩΝ ΣΕ STRING
		canvas.drawLine(90*BitmapWidth/360, 0,90*BitmapWidth/360,(float)(BitmapHeight-BitmapHeight*0.25), pBlack);
		canvas.drawLine(180*BitmapWidth/360, 0,180*BitmapWidth/360,(float)(BitmapHeight-BitmapHeight*0.25), pBlack);
		canvas.drawLine(270*BitmapWidth/360, 0,270*BitmapWidth/360,(float)(BitmapHeight-BitmapHeight*0.25), pBlack);
		canvas.drawLine(0,BitmapHeight/2,BitmapWidth,BitmapHeight/2, pBlack);
		//canvas.drawLine(0,(float)((BitmapHeight/2)-(23.44)*(BitmapHeight/130)),BitmapWidth,(float)((BitmapHeight/2)-(23.44)*(BitmapHeight/130)), pBlack);
		//canvas.drawLine(0,(float)((BitmapHeight/2)+(23.44)*(BitmapHeight/130)),BitmapWidth,(float)((BitmapHeight/2)+(23.44)*(BitmapHeight/130)), pBlack);

		SimpleDateFormat yearf = new SimpleDateFormat("HH:mm");
		String MoonRiseS = "-";
		String MoonSetS = "-";
		if (iMrise>0 && iMrise<=1440){SetRiseTime.setTime(DNext[iMrise]);}
		MoonRiseS = yearf.format(SetRiseTime);
		if (iMrise==1440){ MoonRiseS = "OXI";}	
		if (iMset>0 && iMset<=1440){SetRiseTime.setTime(DNext[iMset]);}
		MoonSetS= yearf.format(SetRiseTime);
		if (iMset==1440){MoonSetS= "OXI";}
		if (iSrise>0 && iSrise<=1440){SetRiseTime.setTime(DNext[iSrise]);}
		String SunRiseS = yearf.format(SetRiseTime);
		if (iSset>0 && iSset<=1440){SetRiseTime.setTime(DNext[iSset]);}
		String SunSetS= yearf.format(SetRiseTime);	
		FText.setText(formattedDatenow+" Σελήνη "+MoonDays+","+ (MoonHrs*100/24+MoonMins*100/1440)+ " ημερών ");  
		canvas.drawCircle(Mlog2, Mlat2, 50, pWhite); 				//ΑΠΟΤΥΠΩΣΗ ΤΗΣ ΣΕΛΗΝΗΣ ΣΤΟ BITMAP
		canvas.drawCircle(Slog2, Slat2, 50, pYellow);				//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΗΛΙΟΥ ΣΤΟ BITMAP
		canvas.drawCircle(Alog2, Alat2, 50, pBlack);	
		canvas.drawCircle(Aslog2, Aslat2, 50, pBlack);//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP
		canvas.drawCircle(Wlong, Wlat, 50, pOrange);//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP
		canvas.drawCircle(AWlong, AWlat, 50, pOrange);//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP	//canvas.drawCircle(Alog2, Alat2, 50, pBlack);				//ΑΠΟΤΥΠΩΣΗ ΤΟΥ ΑΝΤΙΠΟΔΑ ΣΤΟ BITMAP
//βρίσκουμε την μέση τιμή των μηδενισμων των καμύλων
	double[] Chang=new double[5];
	Chang[0]=0;
	Chang[1]=0;
	Chang[2]=0;
	Chang[3]=0;
	String ch0="-";
	String ch1="-";
	String ch2="-";
	String ch3="-";
	if (MSdif[0]>0 && MSdif[1]>0){ Chang[0]=(MSdif[0]+MSdif[1])/2;};
	if (MSdif[0]==0){ Chang[0]=MSdif[1];};if (MSdif[1]==0){ Chang[0]=MSdif[0];};
	
	if (MSdif[2]>0 && MSdif[3]>0){ Chang[1]=(MSdif[2]+MSdif[3])/2;};
	if (MSdif[2]==0){ Chang[1]=MSdif[3];};if (MSdif[3]==0){ Chang[1]=MSdif[2];};
	
	if (MSdif[4]>0 && MSdif[5]>0){ Chang[2]=(MSdif[4]+MSdif[5])/2;};
	if (MSdif[4]==0){ Chang[2]=MSdif[5];};if (MSdif[5]==0){ Chang[2]=MSdif[4];};
	
	if (MSdif[6]>0 && MSdif[7]>0){ Chang[3]=(MSdif[6]+MSdif[7])/2;};
	if (MSdif[6]==0){ Chang[3]=MSdif[7];};if (MSdif[7]==0){ Chang[3]=MSdif[6];};
	
	if (Chang[0]>0 ){ ch0 =yearf.format(Chang[0]);};
	if (Chang[1]>0 ){ ch1 =yearf.format(Chang[1]);};
	if (Chang[2]>0 ){ch2 =yearf.format(Chang[2]);};
	if (Chang[3]>0 ){ ch3 =yearf.format(Chang[3]);};

	//Σχηματιζουμε την καμπύλη ροής
	//if (Wf>360){Wf=Wf-360;}
	if ((180-Wf)>0){AWf=Wf-180;}// f={80,172,255,352}; f={73,165,245,342};
	if ((180-Wf)<0){AWf=Wf+180;}// f={100,8,-75,352};
	int c=(int)(RoundTo2Decimals(180.00-Wf+LocalLong/2));
	int c1=(int)RoundTo2Decimals(180.00-AWf+LocalLong/2);
	int c2=((c+c1)/2)-180;
	int c3=((c+c1)/2);
	float LineHeight=(float)(BitmapHeight-BitmapHeight*0.90);
	int AmWidth=((f2-f1)/2);
	int mWidth= ((f0-(f3-360))/2);
	int Am90=((f1-f0)/2);
	int Am180= ((f3-f2)/2);
	int ig; 
	SetL=0;
//σχηματισμός καμπύλης Wf   RED Θετικές τιμές GREEN Αρνητικές τιμές
	int ki=0;
	for (ig=c-AmWidth; ig<c+AmWidth; ){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight+LineHeight*(float) Math.sin(torad((ig-(c+AmWidth))*180/(AmWidth*2))), pRed);
	if (ki==Math.round(180-LocalLong)){SetL=-(float) Math.sin(torad((ig-(c+AmWidth))*180/(AmWidth*2)));}
	ig++;}			
	canvas.drawText(ch2,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);
//σχηματισμός καμπύλης Wf+90
	for (ig=c3-Am180; ig<=c3+Am180;){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight-LineHeight*(float) Math.sin(torad((ig-(c3+Am180))*180/(Am180*2))), pGreen);
	if (ki==Math.round(180-LocalLong)){SetL=(float) Math.sin(torad((ig-(c3+Am180))*180/(Am180*2)));}
	ig++;}
	canvas.drawText(ch3,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);
//σχηματισμός καμπύλης Wf-90
	for (ig=c2-Am90; ig<=c2+Am90;){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight-LineHeight*(float) Math.sin(torad((ig-(c2+Am90))*180/(Am90*2))), pGreen);
	if (ki==Math.round(180-LocalLong)){SetL=(float) Math.sin(torad((ig-(c2+Am90))*180/(Am90*2)));}
	ig++;}
	canvas.drawText(ch1,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);
//σχηματισμός καμπύλης AWf
	for (ig=c1-mWidth; ig<c1+mWidth;){
		if (ig>=0 && ig<=360){ki=ig;};
		if (ig>360){ki=ig-360;};
		if (ig<0){ki=360+ig;};
	canvas.drawLine(ki*BitmapWidth/360,LineHeight,ki*BitmapWidth/360,LineHeight+LineHeight*(float) Math.sin(torad((ig-(c1+mWidth))*180/(mWidth*2))), pRed);
	if (ki==Math.round(180-LocalLong)){SetL=-(float) Math.sin(torad((ig-(c1+mWidth))*180/(mWidth*2)));}
	ig++;}
	canvas.drawText(ch0,(float)ki*BitmapWidth/360-LineHeight,LineHeight*2, pWhite);

	String A00="-";
	String A11="-";
	String A22="-";
	String A33="-";
	String A000="-";
	String A111="-";
	String A222="-";
	String A333="-";
	String ATest="-";
	if (Chang[0]>0 ){ A00 =yearf.format(Chang[0]-1000000);
					  A000 =yearf.format(Chang[0]+1000000);};
	if (Chang[1]>0 ){ A11 =yearf.format(Chang[1]-1000000);
					  A111 =yearf.format(Chang[1]+1000000);};
	if (Chang[2]>0 ){ A22 =yearf.format(Chang[2]-1000000);
					  A222 =yearf.format(Chang[2]+1000000);};
	if (Chang[3]>0 ){ A33 =yearf.format(Chang[3]-1000000);
					  A333 =yearf.format(Chang[3]+1000000);};


	String f1TS = ("("+A00+"-"+A000+"), ("+A11+"-"+A111+"), ("+A22+"-"+A222+"), ("+A33+"-"+A333+")");
	canvas.drawLine((float)(180-LocalLong)*BitmapWidth/360,0,(float) (180-LocalLong)*BitmapWidth/360,(float)(BitmapHeight*0.18), pYellow);
	if(SetL>0.14){fora="Φορά από νότο με ταχύτητα "+RoundTo2Decimals(SetL*100)+" %";}
	if(SetL<-0.14){fora="Φορά από βορρά με ταχύτητα "+RoundTo2Decimals(-SetL*100)+" %";}
	if(SetL<0.14 && SetL>-0.14){fora="Φάση Αλλαγής Ρεύματος -  Ταχύτητα χαμηλή";}
	F2Text.setText(fora+" "+MaxW);//+" W: "+RoundTo2Decimals(Wf)+" "+M+" A: "+RoundTo2Decimals(DiffSM));
	//if (MSdif[8]>0 ){ ATest =yearf.format(MSdif[8]);}
	//F2Text.setText(" W: "+RoundTo2Decimals(Wf)+" "+M+" A: "+RoundTo2Decimals(DiffSM)+" MaxW: "+RoundTo2Decimals(MaxW)+" "+ATest);
	canvas.drawText(f1TS,0,(float)(BitmapHeight-BitmapHeight*0.02), pYellow);
	canvas.drawText("Προβλεπόμενες αλλαγές ρεύματος :"+Katastasi,0,(float)(BitmapHeight-BitmapHeight*0.12), pYellow);
	canvas.drawText("Ήλιος : "+StrSlong+" - "+StrSlat+"    Σελήνη : "+StrMlog+" - "+StrMlat,0,(float)(BitmapHeight-BitmapHeight*0.21), pWhite);
}
public class LiveStrimingTestActivity extends Activity{
}
//@SuppressLint("SimlpleDateFormat")
@Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      BDate = (Button)findViewById(R.id.BuUp);
      BTime = (Button)findViewById(R.id.BuDown);
      Binfo = (Button)findViewById(R.id.BuInfo);
    //  BMatrix=(Button)findViewById(R.id.BuMatrix);
      BPhase=(Button)findViewById(R.id.BuPhase);
     // ScrolBut=(ScrollView)findViewById(R.id.scrollViewButtons);
      FText = (EditText) findViewById (R.id.FTxt);
      F2Text = (EditText) findViewById (R.id.F2Txt);
      bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
      mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
      canvas = new Canvas(mutableBitmap);
      pWhite.setColor(Color.WHITE);
      pYellow.setColor(Color.YELLOW);
      pBlue.setColor(Color.BLUE);
      pBlack.setColor(Color.BLACK);
      pRed.setColor(Color.RED);
      pOrange.setColor(Color.parseColor("#ff9900"));
      pGreen.setColor(Color.GREEN);
      
      image1 = (ImageView)findViewById(R.id.Image1);
      image1.setAdjustViewBounds(true);
      image1.setImageBitmap(mutableBitmap);
     BitmapWidth=mutableBitmap.getWidth();
     //Densit=mutableBitmap.getDensity();
     BitmapHeight=mutableBitmap.getHeight();
  // Πάχος γραμμων για Midium Dpi
     pBlack.setStrokeWidth(6f);
     pYellow.setStrokeWidth(16f);
     pOrange.setStrokeWidth(6f);
     pRed.setStrokeWidth(10f);
     pBlue.setStrokeWidth(6f);
     pGreen.setStrokeWidth(10f);
     pWhite.setStrokeWidth(6f);
  // Μέγεθος Χαρακτήρων   
     pWhite.setTextSize(100);
     pYellow.setTextSize(100);  
     
    //  Wellcome();
  //   TimeZone.getDefault();
     c =  Calendar.getInstance(TimeZone.getDefault()); 
//     int SWTime = 0;

//     if (c.get(Calendar.MONTH)>=0 && c.get(Calendar.MONTH)<3){SWTime=3;}
    //   if (c.get(Calendar.MONTH)>=10 && c.get(Calendar.MONTH)<=11){SWTime=3;}

  //    c.setTimeInMillis(c.getTimeInMillis()+3*60*60*1000);
     year=c.get(Calendar.YEAR);
     month=c.get(Calendar.MONTH);
     day=c.get(Calendar.DAY_OF_MONTH);
     hour=c.get(Calendar.HOUR);
     minute=c.get(Calendar.MINUTE);
     hour = c.get(Calendar.HOUR_OF_DAY);
     minute = c.get(Calendar.MINUTE);
     //c.set(year, month, day, hour, minute);
     if (month==9 ){RiseCorect=-14;};
     if (month==10 ){RiseCorect=-13;};
     if (month==11){RiseCorect=-10;};
     if (month==0){RiseCorect=-4;SetCorect=5;};
     if (month==1){RiseCorect=-4;SetCorect=12;}; 
     if (month==2){RiseCorect=-1;SetCorect=12;};
     if (month==3){RiseCorect=0;SetCorect=13;};
     if (month==4 ){RiseCorect=-1;SetCorect=13;};
     if (month==5){RiseCorect=-2;SetCorect=9;};
     if (month==6){RiseCorect=-5;SetCorect=4;}; 
     if (month==7){RiseCorect=-10;};
     if (month==8){RiseCorect=-14;};       
     DateNext=  c.getTime();
     SetRiseTime= c.getTime();
     cfull = Calendar.getInstance(TimeZone.getDefault());
     c1o= Calendar.getInstance(TimeZone.getDefault());
     c2o= Calendar.getInstance(TimeZone.getDefault());
     cnew1= Calendar.getInstance(TimeZone.getDefault());
     cnew2= Calendar.getInstance(TimeZone.getDefault());
   //  DisplayMetrics dm = new DisplayMetrics();
  // getWindowManager().getDefaultDisplay().getMetrics(dm);;
 // widthD=dm.widthPixels;
 // heightD=dm.heightPixels;

   //  FText.setWidth(widthD-10); 
   //  F2Text.setWidth(widthD-10); 


    final SimpleDateFormat RoiF = new SimpleDateFormat("dd/MM/yyyy");
  yearf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
  // ScrolBut.setY((float) ((heightD*77)/100));
   MoonFase();
   changefinder();
   RiseAndSet();
    Graphs( );   	
 	

   Binfo.setOnClickListener(new View.OnClickListener() {
       @Override
		public void onClick(View v) 
       {
      	 openInfoActivity();
       }
       });
   BPhase.setOnClickListener(new View.OnClickListener() {
       @Override
		public void onClick(View v) 
       {
      	 MoonFase();
      	 MoonPhaseprocedure();
       }
       });											
     BDate.setOnClickListener(new View.OnClickListener() {
         @Override
			public void onClick(View v) 
         {        	
      	showDialog(DATE_PICKER_ID);
         }
          });
      BTime.setOnClickListener(new View.OnClickListener() {
          @Override
			public void onClick(View v) 
          {        	
          	 showDialog(TIME_PICKER_ID); 
          }
           });
  }
@Override
protected Dialog onCreateDialog(int id) {
 switch (id) {
 case TIME_PICKER_ID:     
     // set time picker as current time
     return new TimePickerDialog(this, timePickerListener, hour, minute, false);
 case DATE_PICKER_ID:
    // open datepicker dialog. 
     // set date picker for current date 
     // add pickerListener listner to date picker
     return new DatePickerDialog(this, pickerListener, year, month,day);
 }
 return null;
}

private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

//  when dialog box is closed, below method will be called.
@Override
 public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
     year  = selectedYear;
     month = selectedMonth;
     day   = selectedDay;
   c.set(year, month, day,hour,minute);
   if (month==9 ){RiseCorect=-14;};
   if (month==10 ){RiseCorect=-13;};
   if (month==11){RiseCorect=-10;};
   if (month==0){RiseCorect=-4;SetCorect=5;};
   if (month==1){RiseCorect=-4;SetCorect=12;}; 
   if (month==2){RiseCorect=-1;SetCorect=12;};
   if (month==3){RiseCorect=0;SetCorect=13;};
   if (month==4 ){RiseCorect=-1;SetCorect=13;};
   if (month==5){RiseCorect=-2;SetCorect=9;};
   if (month==6){RiseCorect=-5;SetCorect=4;}; 
   if (month==7){RiseCorect=-10;};
   if (month==8){RiseCorect=-14;}; 
	DateNext=  c.getTime();
  mutableBitmap.recycle();
  mutableBitmap=null;
  mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	canvas.setBitmap(mutableBitmap);             	
 	image1.setImageBitmap(mutableBitmap);
 MoonFase();
 	changefinder();
 	RiseAndSet();
  Graphs( );   	
   }
};
private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() { 
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
        hour   = hourOfDay;
        minute = minutes;
      c.set(year, month, day, hour, minute);
  	DateNext=  c.getTime();
     mutableBitmap.recycle();
     mutableBitmap=null;
     mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
   	canvas.setBitmap(mutableBitmap);             	
    	image1.setImageBitmap(mutableBitmap);
    //	MoonFase();
     	changefinder();
    	RiseAndSet();
      Graphs( );   	
       }
};
private void RiseAndSet(){
	  //Ευρεση ανατολής Σελήνης  
	    if (RoundTo2Decimals(MoonRise[0])<0.00){//Αν η σελήνη δεν έχει ανατήλει 12 ώρες πριν την επιλεγμένη ώρα
	  	     iMrise=-1;
	  	      iMset=-1;
	  	  do {
	  	  iMrise++;			//Αυξανουμε το iΜRise εφόσον  MoonRise[iMrise]<0.00
	  	  	if (iMrise>1440){break;}}
	  	  while (MoonRise[iMrise]<0.00);//όταν βρίσκουμε πότε το MoonRise[iMrise] γίνεται θετικό θέτουμε κρατάμε το  Mrise 
	  	  iMset=iMrise; 				//και θέτουμε το  iMset=iMrise ώστε να ερχίσει να αυξάνεται μέχρι MoonRise[iMset]>0.00
	    	do {
	  	  iMset++;
	  	  if (iMset>1440){break;}}
	    	while (MoonRise[iMset]>0.00);//όταν βρίσκουμε πότε το MoonRise[iMset] γίνεται αρνητικο θέτουμε κρατάμε το  Mset
	    	}  
	    if (RoundTo2Decimals(MoonRise[0])>0.00){//Αν η σελήνη έχει ανατήλει στις 00:00
	//Βρίσκουμε πότε δύει
	        iMset=-1;
	  	  do {
	  	  iMset++;
	  	  	if (iMset>1440){break;}}
	  	  while (MoonRise[iMset]>0.00);
	  	  iMrise=iMset; //και βλέπουμε αν πότε ξαναΑνατέλει
	  	  do {
	  	  iMrise++;
	  	  	if (iMrise>1440){break;}}
	  	  while (MoonRise[iMrise]<0.00);
	    }   
	    //Ευρεση ανατολής Ήλιου
	    if (RoundTo2Decimals(SunRise[0])<0.00){//Αν ό ήλιος δεν έχει ανατήλει 12 ώρες πριν την επιλεγμένη ώρα

	    iSrise=-1;
	    iSset=-1;
		  do {
	  	  iSrise++;
	  	  	if (iSrise>1440){break;}}
	  	  while (SunRise[iSrise]<0.00); //κρατάμε την τιμή της Ανατολής
	  	  iSset=iSrise;//συνεχίζουμε να βρούμε την δύση.
	    	do {
	  	  iSset++;
	  	  if (iSset>1440){break;}}
	    	while (SunRise[iSset]>0.00);
	    	 iSrise=iSrise+RiseCorect;
	    	 iSset=iSset+SetCorect;}
	    //Τέλος της If 
	    if (RoundTo2Decimals(SunRise[0])>0.00){//Αν ό ήλιος έχει ανατήλει 12 ώρες πριν την επιλεγμένη ώρα

	    iSset=-1;
	    iSrise=-1;
		  do {
	  	  iSset++;
	  	  	if (iSset>1440){break;}}
	  	  while (SunRise[iSset]>0.00); //κρατάμε την τιμή της Δύσης
	  	  iSrise=iSset;//συνεχίζουμε ωα βρούμε την ανατολή.
	    	do {
	  	  iSrise++;
	  	  if (iSrise>1440){break;}}
	    	while (SunRise[iSrise]<0.00);
	    	 iSrise=iSrise+RiseCorect;
	    	 iSset=iSset+SetCorect;}
	    //Τέλος της If 
    
}

@SuppressLint("SimpleDateFormat")


private void MoonFase() {
  String SNew1,S1o,S2o,SFull,SNew2;
     long D1o,D2o,DNew1,DNew2,Dfull,Dnow; 
     double JTimem;
     Cdate = c.getTime();
   // int SWTime = 6;
    //if (Cdate.getMonth()>=0 && Cdate.getMonth()<3){SWTime=2;}
     //if (Cdate.getMonth()>=3 && Cdate.getMonth()<10){SWTime=6;}
      //if (Cdate.getMonth()>=10 && Cdate.getMonth()<=11){SWTime=2;}
     // Cdate.setTime(Cdate.getTime()+SWTime*60*60*1000);
      JDtime(Cdate);
      JTimem=TJD;
    //  JTime=jtime(Cdate);
    phasehunt5(JTimem,test); //ΤΟΠΟΘΕΤΗΣΕΙ ΤΗΣ ΗΜΕΡΟΜΗΝΙΑΣ ΣΤΗΝ ΔΙΑΔΙΚΑΣΙΑ ΓΙΑ ΝΑ ΕΞΑΓΟΥΜΕ ΤΙΣ ΠΕΝΤΕ ΦΑΣΕΙΣ ΤΗΣ ΣΕΛΗΝΗΣ 
   //ευρεση των φασεων της σελήνης μεταξή της ημερομηνίας
   double tlo=test[1]-JTimem;  //1ο ΤΕΤΑΡΤΟ
   double tfull=test[2]-JTimem;//Πανσέληνος
   double t2o=test[3]-JTimem;  //2ο ΤΕΤΑΡΤΟ
   double tnew1=test[0]-JTimem;//ΑΡΧΗ ΝΕΑΣ ΣΕΛΗΝΗΣ
   double tnew2=test[4]-JTimem;//ΕΠΟΜΕΝΗ ΝΕΑ ΣΕΛΗΝΗ

   D1o=Cdate.getTime()+(long) (tlo*86400000);//+SWTime*60*60*1000;
   Dfull=Cdate.getTime()+(long) (tfull*86400000);//+SWTime*60*60*1000;
   D2o=Cdate.getTime()+(long) (t2o*86400000);//+SWTime*60*60*1000;
   DNew1=Cdate.getTime()+(long) (tnew1*86400000);//+SWTime*60*60*1000;     
   DNew2=Cdate.getTime()+(long) (tnew2*86400000);//+SWTime*60*60*1000;
   Dnow=Cdate.getTime();//+SWTime*60*60*1000;
   long PreMoon;  
   PreMoon = MoonDays;
   MoonDays=(Dnow-DNew1)/(24*60*60*1000); //Συνοδικος μήνας σε ημέρες.
   if (MoonDays>29){MoonDays=PreMoon+1;}
   MoonHrs=(Dnow-DNew1)%(24*60*60*1000);	//Η ώρες της σελήνης
   MoonMins=MoonHrs%(60*60*1000);
   MoonMins=MoonMins/(60*1000);
  MoonHrs=24*MoonHrs/86400000;
 cfull.setTimeInMillis(Dfull);
 c1o.setTimeInMillis(D1o);
 c2o.setTimeInMillis(D2o);
 cnew1.setTimeInMillis(DNew1);
 cnew2.setTimeInMillis(DNew2);
 SNew1=yearf.format(cnew1.getTime());
 S1o=yearf.format(c1o.getTime());
 S2o=yearf.format(c2o.getTime());
 SFull=yearf.format(cfull.getTime());
 SNew2=yearf.format(cnew2.getTime());
 PhaseArray[0]=SNew1;
 PhaseArray[1]=S1o;
 PhaseArray[2]=SFull;
 PhaseArray[3]=S2o;
 PhaseArray[4]=SNew2;
}
private void MoonPhaseprocedure(){
	double Az,Alt;
	c.set(year, month, day, hour, minute);
	DateNext=  c.getTime();
	JDtime(DateNext);
	TtimeC(TJD);
	Az=Azimouth();
	SunsLongitude(TJD);
	Alt=Altitude();
	String MoonRiseS = "-";
	String MoonSetS = "-";
	if (iMrise>0 && iMrise<=1440){SetRiseTime.setTime(DNext[iMrise]);}
	MoonRiseS = yearf.format(SetRiseTime);
	if (iMrise==1440){ MoonRiseS = "OXI";}	
	if (iMset>0 && iMset<=1440){SetRiseTime.setTime(DNext[iMset]);}
	MoonSetS= yearf.format(SetRiseTime);
	if (iMset==1440){MoonSetS= "OXI";}
	if (iSrise>0 && iSrise<=1440){SetRiseTime.setTime(DNext[iSrise]);}
	String SunRiseS = yearf.format(SetRiseTime);
	if (iSset>0 && iSset<=1440){SetRiseTime.setTime(DNext[iSset]);}
	String SunSetS= yearf.format(SetRiseTime);	
  Intent i = new Intent(getApplicationContext(), PhaseActivity.class);
 i.putExtra("PhaseSend", PhaseArray);
 i.putExtra("MoonDistance", MOONDIST);
 i.putExtra("MoonAzimouth", RoundTo2Decimals(Az));
 i.putExtra("MoonAltitude", RoundTo2Decimals(Alt));
 i.putExtra("SunDistance", RoundTo2Decimals(SunDistance));
 i.putExtra("SunAltitude", RoundTo2Decimals(Math.round(SunAlt)));
 i.putExtra("SunDeclinition", RoundTo2Decimals(Math.round(SunDecl)));
 i.putExtra("Sun_Rise", SunRiseS);
 i.putExtra("Sun_Set", SunSetS);
 i.putExtra("Moon_Rise", MoonRiseS);
 i.putExtra("Moon_Set", MoonSetS);
 i.putExtra("SMAngle", AngleSM);
  startActivity(i);
}
public void openInfoActivity(){

  Intent i1 = new Intent(getApplicationContext(), InfoActivity.class);
  
  startActivity(i1);
}
//Find time of phases of the moon which surround the current
//date.  Five phases are found, starting and ending with the
//new moons which bound the current lunation.
public  void phasehunt5( double sdate, double[] phases )
	{
	double adate, nt1, nt2;
	RefDouble k1 = new RefDouble();
	RefDouble k2 = new RefDouble();

	adate = sdate - 45;
	nt1 = meanphase(adate, 0.0, k1);
	
	for (;;)
	    {
	    adate += synmonth;
	    nt2 = meanphase(adate, 0.0, k2);
	    if (nt1 <= sdate && nt2 > sdate)
	        break;
	    nt1 = nt2;
	    k1.val = k2.val;
	    }
	
	phases[0] = truephase(k1.val, 0.0);
	phases[1] = truephase(k1.val, 0.25);
	phases[2] = truephase(k1.val, 0.5);
	phases[3] = truephase(k1.val, 0.75);//
	phases[4] = truephase(k2.val, 0.0);
	
	}

}