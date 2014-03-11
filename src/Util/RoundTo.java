package Util;

public class RoundTo {

	public static float RoundUpToNearest(float passedNumber, float roundTo) {
		
		//105.5 up to nearest 1 = 106;
		//105.5 up to nearest 10 = 110;
		//105.5 up to nearest 7 = 112;
		//105.5 up to nearest 100 = 200;
		//105.5 up to nearest 0.2 = 105.6;
		//105.5 up to nearest 0.3 = 105.6;
		
		//if no round to then just pass original number back
		if(roundTo == 0) {
			return passedNumber;
		} else {
			return (float)(Math.ceil(passedNumber / roundTo) * roundTo);
		}
	}
	
	public static float RoundDownToNearest(float passedNumber, float roundTo) {
		
		//105.5 down to nearest 1 = 105
		//105.5 down to nearest 10 = 100
		//105.5 down to nearest 7 = 105
		//105.5 down to nearest 100 = 100
		//105.5 down to nearest 0.2 = 105.4
		//105.5 down to nearest 0.3 = 105.3
		
		//if no roundTo then just pass back original number
		if(roundTo == 0) {
			return passedNumber;
		} else {
			return (float)(Math.floor(passedNumber / roundTo) * roundTo);
		}
	}
	
	
}
