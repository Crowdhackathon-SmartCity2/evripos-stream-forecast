package com.philcham.evriposstream5;


import java.text.SimpleDateFormat;
import java.util.Calendar;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


public class activity_result extends Activity{




/** Called when the activity is first created. */

public Button mback;
public TextView Result;
public String[] BN1=  {"00:01","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00","06:30"}; //1st day
public String[] NB1=  {"06:20","06:50","07:20","07:50","08:20","08:50","09:20","09:50","10:20","10:50","11:20","11:50","12:20","12:50"}; //1st day
public String[] BN2=  {"12:15","12:45","13:15","13:45","14:15","14:45","15:15","15:45","16:15","16:45","17:15","17:45","18:15","18:45"}; //1st day
public String[] NB2=  {"18:36","19:06","19:36","20:06","20:36","21:06","21:36","22:06","22:36","23:06","23:36","00:06","01:36","02:06"}; //1st day
public String[] AKA=  {"ΑΚΑ","ΤΑ","ΣΤΑ","ΤΑ"}; //1st day
final Context context = this;

public TextView Phasearray[]= new TextView [31];
public TextView BN1array[]= new TextView [31];
public TextView NB1array[]= new TextView [31];
public TextView BN2array[]= new TextView [31];
public TextView NB2array[]= new TextView [31];

public Calendar c;
public  SimpleDateFormat yearf2;
public ScrollView Sc;
public float TestS;
public int Roiarray[]= new int [31];
public String[] PhaseAr2=new String [31];

	  /** Called when the activity is first created. */
private void Wellcome(){
	 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

			// set title
			alertDialogBuilder.setTitle("EVRIPOS STREAM");

			// set dialog message
			alertDialogBuilder
				.setMessage("Ο πίνακας της ροής δείχνει γιά όλο τον συνοδικό μήνα της σελήνης την ώρα που αρχίζει να αλλάζει θεωρητικά η κατεύθυνση "
						+ "της φοράς του ρεύματος του Ευρίπου συν-πλην 20 λεπτά περίπου"
                        + "This matrix shows us, for the whole synodic moon month, the time which,theoretically, begins Evripos stream direction changing, +- 20 minutes")
 				.setCancelable(false)

				.setNegativeButton("Continue",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
}
 //@SuppressWarnings("static-access")
@Override
public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_result);

     Sc= (ScrollView) findViewById (R.id.scrollView1);
     NB2array[0]		=	(TextView) findViewById(R.id.WINSTILON0);
     NB2array[1]		=	(TextView) findViewById(R.id.WINSTILON1);
     NB2array[2]		=	(TextView) findViewById(R.id.WINSTILON2);
     NB2array[3]		=	(TextView) findViewById(R.id.WINSTILON3);
     NB2array[4]		=	(TextView) findViewById(R.id.WINSTILON4);
     NB2array[5]		=	(TextView) findViewById(R.id.WINSTILON5);
     NB2array[6]		=	(TextView) findViewById(R.id.WINSTILON6);
    NB2array[7]		=	(TextView) findViewById(R.id.WINSTILON7);
    NB2array[8]		=	(TextView) findViewById(R.id.WINSTILON8);
    NB2array[9]		=	(TextView) findViewById(R.id.WINSTILON9);
    NB2array[10]	=	(TextView) findViewById(R.id.WINSTILON10);
    NB2array[11]	=	(TextView) findViewById(R.id.WINSTILON11);
    NB2array[12]	=	(TextView) findViewById(R.id.WINSTILON12);
    NB2array[13]	=	(TextView) findViewById(R.id.WINSTILON13);
    NB2array[14]	=	(TextView) findViewById(R.id.WINSTILON14);
    NB2array[15]	=	(TextView) findViewById(R.id.WINSTILON15);
    NB2array[16]		=	(TextView) findViewById(R.id.WINSTILON16);
    NB2array[17]		=	(TextView) findViewById(R.id.WINSTILON17);
    NB2array[18]		=	(TextView) findViewById(R.id.WINSTILON18);
    NB2array[19]		=	(TextView) findViewById(R.id.WINSTILON19);
    NB2array[20]		=	(TextView) findViewById(R.id.WINSTILON20);
    NB2array[21]		=	(TextView) findViewById(R.id.WINSTILON21);
    NB2array[22]		=	(TextView) findViewById(R.id.WINSTILON22);
    NB2array[23]		=	(TextView) findViewById(R.id.WINSTILON23);
    NB2array[24]		=	(TextView) findViewById(R.id.WINSTILON24);
    NB2array[25]		=	(TextView) findViewById(R.id.WINSTILON25);
    NB2array[26]		=	(TextView) findViewById(R.id.WINSTILON26);
    NB2array[27]		=	(TextView) findViewById(R.id.WINSTILON27);
    NB2array[28]		=	(TextView) findViewById(R.id.WINSTILON28);
    NB2array[29]		=	(TextView) findViewById(R.id.WINSTILON29);
    
    
    
    Phasearray[0]		=	(TextView) findViewById(R.id.SYSTIMA0);
    Phasearray[1]		=	(TextView) findViewById(R.id.SYSTIMA1);
    Phasearray[2]		=	(TextView) findViewById(R.id.SYSTIMA2);
    Phasearray[3]		=	(TextView) findViewById(R.id.SYSTIMA3);
    Phasearray[4]		=	(TextView) findViewById(R.id.SYSTIMA4);
    Phasearray[5]		=	(TextView) findViewById(R.id.SYSTIMA5);
    Phasearray[6]		=	(TextView) findViewById(R.id.SYSTIMA6);
    Phasearray[7]		=	(TextView) findViewById(R.id.SYSTIMA7);
   Phasearray[8]		=	(TextView) findViewById(R.id.SYSTIMA8);
   Phasearray[9]		=	(TextView) findViewById(R.id.SYSTIMA9);
   Phasearray[10]	=	(TextView) findViewById(R.id.SYSTIMA10);
   Phasearray[11]	=	(TextView) findViewById(R.id.SYSTIMA11);
   Phasearray[12]	=	(TextView) findViewById(R.id.SYSTIMA12);
   Phasearray[13]	=	(TextView) findViewById(R.id.SYSTIMA13);
   Phasearray[14]	=	(TextView) findViewById(R.id.SYSTIMA14);
   Phasearray[15]	=	(TextView) findViewById(R.id.SYSTIMA15);
   Phasearray[16]		=	(TextView) findViewById(R.id.SYSTIMA16);
   Phasearray[17]		=	(TextView) findViewById(R.id.SYSTIMA17);
   Phasearray[18]		=	(TextView) findViewById(R.id.SYSTIMA18);
   Phasearray[19]		=	(TextView) findViewById(R.id.SYSTIMA19);
   Phasearray[20]		=	(TextView) findViewById(R.id.SYSTIMA20);
   Phasearray[21]		=	(TextView) findViewById(R.id.SYSTIMA21);
   Phasearray[22]		=	(TextView) findViewById(R.id.SYSTIMA22);
   Phasearray[23]		=	(TextView) findViewById(R.id.SYSTIMA23);
   Phasearray[24]		=	(TextView) findViewById(R.id.SYSTIMA24);
   Phasearray[25]		=	(TextView) findViewById(R.id.SYSTIMA25);
   Phasearray[26]		=	(TextView) findViewById(R.id.SYSTIMA26);
   Phasearray[27]		=	(TextView) findViewById(R.id.SYSTIMA27);
   Phasearray[28]		=	(TextView) findViewById(R.id.SYSTIMA28);
   Phasearray[29]		=	(TextView) findViewById(R.id.SYSTIMA29);

   BN1array[0]	=	(TextView) findViewById(R.id.STILES0);
   BN1array[1]		=	(TextView) findViewById(R.id.STILES1);
  BN1array[2]		=	(TextView) findViewById(R.id.STILES2);
  BN1array[3]		=	(TextView) findViewById(R.id.STILES3);
  BN1array[4]		=	(TextView) findViewById(R.id.STILES4);
  BN1array[5]		=	(TextView) findViewById(R.id.STILES5);
  BN1array[6]		=	(TextView) findViewById(R.id.STILES6);
  BN1array[7]		=	(TextView) findViewById(R.id.STILES7);
  BN1array[8]		=	(TextView) findViewById(R.id.STILES8);
  BN1array[9]		=	(TextView) findViewById(R.id.STILES9);
  BN1array[10]	=	(TextView) findViewById(R.id.STILES10);
  BN1array[11]	=	(TextView) findViewById(R.id.STILES11);
  BN1array[12]	=	(TextView) findViewById(R.id.STILES12);
  BN1array[13]	=	(TextView) findViewById(R.id.STILES13);
  BN1array[14]	=	(TextView) findViewById(R.id.STILES14);
  BN1array[15]	=	(TextView) findViewById(R.id.STILES15);
  BN1array[16]		=	(TextView) findViewById(R.id.STILES16);
  BN1array[17]		=	(TextView) findViewById(R.id.STILES17);
  BN1array[18]		=	(TextView) findViewById(R.id.STILES18);
  BN1array[19]		=	(TextView) findViewById(R.id.STILES19);
  BN1array[20]		=	(TextView) findViewById(R.id.STILES20);
  BN1array[21]		=	(TextView) findViewById(R.id.STILES21);
  BN1array[22]		=	(TextView) findViewById(R.id.STILES22);
  BN1array[23]		=	(TextView) findViewById(R.id.STILES23);
  BN1array[24]		=	(TextView) findViewById(R.id.STILES24);
  BN1array[25]		=	(TextView) findViewById(R.id.STILES25);
  BN1array[26]		=	(TextView) findViewById(R.id.STILES26);
  BN1array[27]		=	(TextView) findViewById(R.id.STILES27);
  BN1array[28]		=	(TextView) findViewById(R.id.STILES28);
  BN1array[29]		=	(TextView) findViewById(R.id.STILES29);

  NB1array[0]		=	(TextView) findViewById(R.id.POLSYSTIM0);
NB1array[1]		=	(TextView) findViewById(R.id.POLSYSTIM1);
NB1array[2]		=	(TextView) findViewById(R.id.POLSYSTIM2);
NB1array[3]		=	(TextView) findViewById(R.id.POLSYSTIM3);
NB1array[4]		=	(TextView) findViewById(R.id.POLSYSTIM4);
NB1array[5]		=	(TextView) findViewById(R.id.POLSYSTIM5);
NB1array[6]		=	(TextView) findViewById(R.id.POLSYSTIM6);
NB1array[7]		=	(TextView) findViewById(R.id.POLSYSTIM7);
NB1array[8]		=	(TextView) findViewById(R.id.POLSYSTIM8);
NB1array[9]		=	(TextView) findViewById(R.id.POLSYSTIM9);
NB1array[10]	=	(TextView) findViewById(R.id.POLSYSTIM10);
NB1array[11]	=	(TextView) findViewById(R.id.POLSYSTIM11);
NB1array[12]	=	(TextView) findViewById(R.id.POLSYSTIM12);
NB1array[13]	=	(TextView) findViewById(R.id.POLSYSTIM13);
NB1array[14]	=	(TextView) findViewById(R.id.POLSYSTIM14);
NB1array[15]	=	(TextView) findViewById(R.id.POLSYSTIM15);
NB1array[16]		=	(TextView) findViewById(R.id.POLSYSTIM16);
NB1array[17]		=	(TextView) findViewById(R.id.POLSYSTIM17);
NB1array[18]		=	(TextView) findViewById(R.id.POLSYSTIM18);
NB1array[19]		=	(TextView) findViewById(R.id.POLSYSTIM19);
NB1array[20]		=	(TextView) findViewById(R.id.POLSYSTIM20);
NB1array[21]		=	(TextView) findViewById(R.id.POLSYSTIM21);
NB1array[22]		=	(TextView) findViewById(R.id.POLSYSTIM22);
NB1array[23]		=	(TextView) findViewById(R.id.POLSYSTIM23);
NB1array[24]		=	(TextView) findViewById(R.id.POLSYSTIM24);
NB1array[25]		=	(TextView) findViewById(R.id.POLSYSTIM25);
NB1array[26]		=	(TextView) findViewById(R.id.POLSYSTIM26);
NB1array[27]		=	(TextView) findViewById(R.id.POLSYSTIM27);
NB1array[28]		=	(TextView) findViewById(R.id.POLSYSTIM28);
NB1array[29]		=	(TextView) findViewById(R.id.POLSYSTIM29);

BN2array[0]		=	(TextView) findViewById(R.id.SYNSTILES0);
BN2array[1]		=	(TextView) findViewById(R.id.SYNSTILES1);
 BN2array[2]		=	(TextView) findViewById(R.id.SYNSTILES2);
 BN2array[3]		=	(TextView) findViewById(R.id.SYNSTILES3);
 BN2array[4]		=	(TextView) findViewById(R.id.SYNSTILES4);
 BN2array[5]		=	(TextView) findViewById(R.id.SYNSTILES5);
 BN2array[6]		=	(TextView) findViewById(R.id.SYNSTILES6);
 BN2array[7]		=	(TextView) findViewById(R.id.SYNSTILES7);
BN2array[8]		=	(TextView) findViewById(R.id.SYNSTILES8);
BN2array[9]		=	(TextView) findViewById(R.id.SYNSTILES9);
BN2array[10]	=	(TextView) findViewById(R.id.SYNSTILES10);
BN2array[11]	=	(TextView) findViewById(R.id.SYNSTILES11);
BN2array[12]	=	(TextView) findViewById(R.id.SYNSTILES12);
BN2array[13]	=	(TextView) findViewById(R.id.SYNSTILES13);
BN2array[14]	=	(TextView) findViewById(R.id.SYNSTILES14);
BN2array[15]	=	(TextView) findViewById(R.id.SYNSTILES15);
BN2array[16]		=	(TextView) findViewById(R.id.SYNSTILES16);
BN2array[17]		=	(TextView) findViewById(R.id.SYNSTILES17);
BN2array[18]		=	(TextView) findViewById(R.id.SYNSTILES18);
BN2array[19]		=	(TextView) findViewById(R.id.SYNSTILES19);
BN2array[20]		=	(TextView) findViewById(R.id.SYNSTILES20);
BN2array[21]		=	(TextView) findViewById(R.id.SYNSTILES21);
BN2array[22]		=	(TextView) findViewById(R.id.SYNSTILES22);
BN2array[23]		=	(TextView) findViewById(R.id.SYNSTILES23);
BN2array[24]		=	(TextView) findViewById(R.id.SYNSTILES24);
BN2array[25]		=	(TextView) findViewById(R.id.SYNSTILES25);
BN2array[26]		=	(TextView) findViewById(R.id.SYNSTILES26);
BN2array[27]		=	(TextView) findViewById(R.id.SYNSTILES27);
BN2array[28]		=	(TextView) findViewById(R.id.SYNSTILES28);
BN2array[29]		=	(TextView) findViewById(R.id.SYNSTILES29);
Wellcome();
 
 Bundle B=getIntent().getExtras();
 if (B != null) {
 PhaseAr2=B.getStringArray("MoonDateSend");
Roiarray=B.getIntArray("RoiDaySend");
//int f1 = 0;
//c.getInstance();
 }
for (int f=0;f<30;){
	
	int R=Roiarray[f]-1; 
	Phasearray[f].setText(new StringBuilder().append(PhaseAr2[f]));
	if (R>-1){
	if (R<=BN1.length){BN1array[f].setText(new StringBuilder().append(BN1[R]));}
	if (R<=NB1.length){NB1array[f].setText(new StringBuilder().append(NB1[R]));}
	if (R<=BN2.length){BN2array[f].setText(new StringBuilder().append(BN2[R]));}
	if (R<=NB2.length){NB2array[f].setText(new StringBuilder().append(NB2[R]));}
	}
	if (R<-1){
	BN1array[f].setText(new StringBuilder().append(AKA[0]));
	NB1array[f].setText(new StringBuilder().append(AKA[1]));
	BN2array[f].setText(new StringBuilder().append(AKA[2]));
	NB2array[f].setText(new StringBuilder().append(AKA[3]));
	}	
	f++; 
}



 }
}


    
