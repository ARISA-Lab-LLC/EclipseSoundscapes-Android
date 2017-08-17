package org.eclipsesoundscapes.util;

import java.util.Locale;

/**
 * Created by horus on 7/30/17.
 */



public class EclipseTimeGenerator {

    public EclipseType type = EclipseType.FULL;

    private Double longitude;
    private Double latitude;


    private boolean isEclipse = true;
    private boolean isPartial = false;

    private double R2D = 180.0 / Math.PI;
    private double D2R = Math.PI / 180;


    private Double[] obsvconst = new Double[10];
    private Double[] elements = {2457987.268521, 18.0, -4.0, 4.0, 68.4, -0.12957627, 0.54064089,
            -0.00002930, -0.00000809, 0.48541746, -0.14163940, -0.00009049,
            0.00000205, 11.86696720, -0.01362158, -0.00000249, 89.24544525,
            15.00393677, 0.00000149, 0.54211175, 0.00012407, -0.00001177,
            -0.00402530, 0.00012346, -0.00001172, 0.00462223, 0.00459921};


    private Double[] c1 = new Double[40];
    private Double[] c2 = new Double[40];
    private Double[] mid = new Double[40];
    private Double[] c3 = new Double[40];
    private Double[] c4 = new Double[40];


    public EclipseTimeGenerator(Double latitude, Double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        loc_circ(latitude, longitude);
        printTimes();
    }

    public String latString() {
        return String.format(Locale.getDefault(), "%.3f\u00B0%s", Math.abs(latitude), latitude > 0 ? "North" : "South");
    }


    public String lonString() {
        return String.format(Locale.getDefault(), "%.3f\u00B0%s", Math.abs(longitude), longitude > 0 ? "East" : "West");
    }


    public String getMagnitude() {
        if (type == EclipseType.NONE)
            return null;
        else
            return String.valueOf(Math.round(mid[34] * 1000) / 1000);
    }


    public String duration() {
        if (type == EclipseType.FULL)
            return getduration();
        else
            return null;
    }


    public String coverage() {
        if (type == EclipseType.FULL)
            return getcoverage();
        else
            return null;
    }

    public EclipseEvent contact1() {
        return new EclipseEvent("Start of partial eclipse", getdate(c1), gettime(c1), getalt(c1), getazi(c1));
    }

    public EclipseEvent contact2() {
        return new EclipseEvent("Start of total eclipse", getdate(c2), gettime(c2), getalt(c2), getazi(c2));
    }

    public EclipseEvent contactMid() {
        return new EclipseEvent("Maximum eclipse", getdate(mid), gettime(mid), getalt(mid), getazi(mid));
    }

    public EclipseEvent contact3() {
        return new EclipseEvent("End of total eclipse", getdate(c3), gettime(c3), getalt(c3), getazi(c3));
    }

    public EclipseEvent contact4() {
        return new EclipseEvent("End of partial eclipse", getdate(c4), gettime(c4), getalt(c4), getazi(c4));
    }


    // Populate the circumstances array with the time-only dependent circumstances (x, y, d, m, ...)
    public Double[] timeDependent(Double[] circumstances) {

        Double t = circumstances[1];

        Double ans = elements[8] * t + elements[7];
        ans = ans * t + elements[6];
        ans = ans * t + elements[5];
        circumstances[2] = ans;
        // dx
        ans = 3.0 * elements[8] * t + 2.0 * elements[7];
        ans = ans * t + elements[6];
        circumstances[10] = ans;
        // y
        ans = elements[12] * t + elements[11];
        ans = ans * t + elements[10];
        ans = ans * t + elements[9];
        circumstances[3] = ans;
        // dy
        ans = 3.0 * elements[12] * t + 2.0 * elements[11];
        ans = ans * t + elements[10];
        circumstances[11] = ans;
        // d
        ans = elements[15] * t + elements[14];
        ans = ans * t + elements[13];
        ans *= D2R;
        circumstances[4] = ans;
        // sin d and cos d
        circumstances[5] = Math.sin(ans);
        circumstances[6] = Math.cos(ans);
        // dd
        ans = 2.0 * elements[15] * t + elements[14];
        ans *= D2R;
        circumstances[12] = ans;
        // m
        ans = elements[18] * t + elements[17];
        ans = ans * t + elements[16];
        if (ans >= 360.0) {
            ans -= 360.0;
        }

        ans *= D2R;
        circumstances[7] = ans;
        // dm
        ans = 2.0 * elements[18] * t + elements[17];
        ans *= D2R;
        circumstances[13] = ans;

        // l1 and dl1
        Double type = circumstances[0];
        if (type == -2 || type == 0 || type == 2) {
            ans = elements[21] * t + elements[20];
            ans = ans * t + elements[19];
            circumstances[8] = ans;
            circumstances[14] = 2.0 * elements[21] * t + elements[20];
        }

        // l2 and dl2
        if (type == -1 || type == 0 || type == 1) {
            ans = elements[24] * t + elements[23];
            ans = ans * t + elements[22];
            circumstances[9] = ans;
            circumstances[15] = 2.0 * elements[24] * t + elements[23];
        }

        return circumstances;

    }

    // Populate the circumstances array with the time and location dependent circumstances
    public Double[] timeLocalDependent(Double[] circumstances) {

        circumstances = timeDependent(circumstances);

        //h, sin h, cos h
        circumstances[16] = circumstances[7] - obsvconst[1] - (elements[4] / 13713.44);
        circumstances[17] = Math.sin(circumstances[16]);
        circumstances[18] = Math.cos(circumstances[16]);
        //xi
        circumstances[19] = obsvconst[5] * circumstances[17];
        //eta
        circumstances[20] = obsvconst[4] * circumstances[6] - obsvconst[5] * circumstances[18] * circumstances[5];
        //zeta
        circumstances[21] = obsvconst[4] * circumstances[5] + obsvconst[5] * circumstances[18] * circumstances[6];
        //dxi
        circumstances[22] = circumstances[13] * obsvconst[5] * circumstances[18];
        //deta
        circumstances[23] = circumstances[13] * circumstances[19] * circumstances[5] - circumstances[21] * circumstances[12];
        // u
        circumstances[24] = circumstances[2] - circumstances[19];
        // v
        circumstances[25] = circumstances[3] - circumstances[20];
        // a
        circumstances[26] = circumstances[10] - circumstances[22];
        // b
        circumstances[27] = circumstances[11] - circumstances[23];
        // l1'
        Double type = circumstances[0];
        if (type == -2 || type == 0 || type == 2) {
            circumstances[28] = circumstances[8] - circumstances[21] * elements[25];
        }
        // l2'
        if (type == -1 || type == 0 || type == 1) {
            circumstances[29] = circumstances[9] - circumstances[21] * elements[26];
        }
        // n^2
        circumstances[30] = circumstances[26] * circumstances[26] + circumstances[27] * circumstances[27];

        return circumstances;
    }

    public Double[] c1c4iterate(Double[] circumstances) {
        Double sign = 0.0;
        Double n = 0.0;

        circumstances = timeLocalDependent(circumstances);
        if (circumstances[0] < 0) {
            sign = -1.0;
        } else {
            sign = 1.0;
        }

        Double tmp = 1.0;
        int iter = 0;
        while ((tmp > 0.000001 || tmp < -0.000001) && iter < 50) {
            n = Math.sqrt(circumstances[30]);
            tmp = circumstances[26] * circumstances[25] - circumstances[24] * circumstances[27];
            tmp = tmp / n / circumstances[28];
            tmp = sign * Math.sqrt(1.0 - tmp * tmp) * circumstances[28] / n;
            tmp = (circumstances[24] * circumstances[26] + circumstances[25] * circumstances[27]) / circumstances[30] - tmp;
            circumstances[1] = circumstances[1] - tmp;
            circumstances = timeLocalDependent(circumstances);
            iter += 1;
        }

        return circumstances;
    }

    // Get C1 and C4 data
    //    Entry conditions -
    //    1. The mid array must be populated
    //    2. The magnitude at mid eclipse must be > 0.0
    public void getc1c4() {
        Double n = Math.sqrt(mid[30]);
        Double tmp = mid[26] * mid[25] - mid[24] * mid[27];
        tmp = tmp / n / mid[28];
        tmp = Math.sqrt(1.0 - tmp * tmp) * mid[28] / n;
        c1[0] = -2.0;
        c4[0] = 2.0;
        c1[1] = mid[1] - tmp;
        c4[1] = mid[1] + tmp;
        c1 = c1c4iterate(c1);
        c4 = c1c4iterate(c4);
    }

    // Iterate on C2 or C3
    public Double[] c2c3iterate(Double[] circumstances) {
        Double sign = 0.0;
        Double n = 0.0;

        circumstances = timeLocalDependent(circumstances);
        if (circumstances[0] < 0) {
            sign = -1.0;
        } else {
            sign = 1.0;
        }

        if (mid[29] < 0.0) {
            sign = -sign;
        }

        Double tmp = 1.0;
        int iter = 0;
        while ((tmp > 0.000001 || tmp < -0.000001) && iter < 50) {
            n = Math.sqrt(circumstances[30]);
            tmp = circumstances[26] * circumstances[25] - circumstances[24] * circumstances[27];
            tmp = tmp / n / circumstances[29];
            tmp = sign * Math.sqrt(1.0 - tmp * tmp) * circumstances[29] / n;
            tmp = (circumstances[24] * circumstances[26] + circumstances[25] * circumstances[27]) / circumstances[30] - tmp;
            circumstances[1] = circumstances[1] - tmp;
            circumstances = timeLocalDependent(circumstances);
            iter += 1;
        }

        return circumstances;
    }

    // Get C2 and C3 data
    //    Entry conditions -
    //    1. The mid array must be populated
    //    2. There must be either a total or annular eclipse at the location!
    public void getc2c3() {
        Double n = Math.sqrt(mid[30]);
        Double tmp = mid[26] * mid[25] - mid[24] * mid[27];
        tmp = tmp / n / mid[29];
        tmp = Math.sqrt(1.0 - tmp * tmp) * mid[29] / n;
        c2[0] = -1.0;
        c3[0] = 1.0;
        if (mid[29] < 0.0) {
            c2[1] = mid[1] + tmp;
            c3[1] = mid[1] - tmp;
        } else {
            c2[1] = mid[1] - tmp;
            c3[1] = mid[1] + tmp;
        }
        c2 = c2c3iterate(c2);
        c3 = c2c3iterate(c3);
    }

    // Get the observational circumstances
    public Double[] observational(Double[] circumstances) {

        // alt
        Double sinlat = Math.sin(obsvconst[0]);
        Double coslat = Math.cos(obsvconst[0]);
        circumstances[31] = Math.asin(circumstances[5] * sinlat + circumstances[6] * coslat * circumstances[18]);
        // azi
        circumstances[32] = Math.atan2(-1.0 * circumstances[17] * circumstances[6],
                circumstances[5] * coslat - circumstances[18] * sinlat * circumstances[6]);

        return circumstances;
    }

    // Calculate max eclipse
    public void getmid() {
        mid[0] = 0.0;
        mid[1] = 0.0;
        int iter = 0;
        Double tmp = 1.0;
        mid = timeLocalDependent(mid);
        while ((tmp > 0.000001 || tmp < -0.000001) && iter < 50) {
            tmp = (mid[24] * mid[26] + mid[25] * mid[27]) / mid[30];
            mid[1] = mid[1] - tmp;
            iter += 1;
            mid = timeLocalDependent(mid);
        }
    }

    // Populate the c1, c2, mid, c3 and c4 arrays
    public void getall() {
        getmid();
        mid = observational(mid);
        // m, magnitude and moon/sun ratio
        mid[33] = Math.sqrt(mid[24]*mid[24] + mid[25]*mid[25]);
        mid[34] = (mid[28] - mid[33]) / (mid[28] + mid[29]);
        mid[35] = (mid[28] - mid[29]) / (mid[28] + mid[29]);
        if (mid[34] > 0.0) {
            getc1c4();
            if (mid[33] < mid[29] || mid[33] < -mid[29]) {
                getc2c3();
                if (mid[29] < 0.0) {
                    mid[36] = 3.0; // Total solar eclipse
                } else {
                    mid[36] = 2.0; // Annular solar eclipse
                }
                c2 = observational(c2);
                c3 = observational(c3);
                c2[33] = 999.9;
                c3[33] = 999.9;
            } else {
                mid[36] = 1.0; // Partial eclipse
            }
            c1 = observational(c1);
            c4 = observational(c4);
        } else {
            mid[36] = 0.0; // No eclipse
        }
    }

    // Read the data, and populate the obsvconst array
    public void readdata(Double lat, Double lon) {
        // Get the latitude
        obsvconst[0] = lat;
        obsvconst[0] *= 1;
        obsvconst[0] *= D2R;

        // Get the longitude
        obsvconst[1] = lon;
        obsvconst[1] *= -1;
        obsvconst[1] *= D2R;

        // Get the altitude (sea level by default)
        obsvconst[2] = 0.0;

        // Get the time zone (UT by default)
        obsvconst[3] = 0.0;

        // Get the observer's geocentric position
        Double tmp = Math.atan(0.99664719 * Math.tan(obsvconst[0]));
        obsvconst[4] = 0.99664719 * Math.sin(tmp) + (obsvconst[2] / 6378140.0) * Math.sin(obsvconst[0]);
        obsvconst[5] = Math.cos(tmp) + (obsvconst[2] / 6378140.0 * Math.cos(obsvconst[0]));
    }

    // This is used in getday()
    // Pads digits
    public String padDigits(Double n, int totalDigits){
        String nString = String.format(Locale.getDefault(), "%.0f", n);

        String pd = "";
        if (totalDigits > nString.length()) {
            for (int i = 0; i < totalDigits - nString.length(); i++){
                pd += "0";
            }
        }
        return  String.format(Locale.getDefault(), "%s%s", pd, nString);
    }

    // Get the local date
    public String getdate(Double[] circumstances){

        Double jd = elements[0];

        // Calculate the local time.
        // Assumes JD > 0 (uses same algorithm as SKYCAL)
        Double t = circumstances[1] + elements[1] - obsvconst[3] - (elements[4] - 0.05) / 3600.0;
        if (t < 0.0) {
            t += 24.0; // and jd-- below
        } else if (t >= 24.0) {
            t -= 24.0; // and jd++ below
        }
        Double a = 0.0;
        Double y = 0.0; // Year
        Double m = 0.0; // Month
        Double day = 0.0;
        Double jdm = jd + 0.5;
        Double z = Math.floor(jdm);
        Double f = jdm - z;
        if (z < 2299161) {
            a = z;
        } else if (z >= 2299161) {
            Double alpha = Math.floor((z - 1867216.25) / 36524.25);
            a = z + 1 + alpha - Math.floor(alpha / 4);
        }
        Double b = a + 1524;
        Double c = Math.floor((b - 122.1) / 365.25);
        Double d = Math.floor(365.25 * c);
        Double e = Math.floor((b - d) / 30.6001);
        day = b - d - Math.floor(30.6001 * e) + f;

        if (e < 14) {
            m = e - 1.0;
        } else if (e == 14 || e == 15) {
            m = e - 13.0;
        }

        if (m > 2) {
            y = c - 4716.0;
        } else if (m == 1 || m == 2) {
            y = c - 4715.0;
        }
        Double timediff = t - 24 * (day - Math.floor(day)); // present time minus UT at GE
        if (timediff < -12) {
            day += 1;
        } else if (timediff > 12) {
            day -= 1;
        }

        return String.format(Locale.getDefault(), "%.0f-%.0f-%.0f", Math.floor(m), Math.floor(day), Math.floor(y));
    }


    // Get the local time
    public String gettime(Double[] circumstances){
        String ans = "";

        Double t = circumstances[1] + elements[1] - obsvconst[3] - (elements[4] - 0.05) / 3600.0;
        if (t < 0.0) {
            t += 24.0;
        } else if (t >= 24.0) {
            t -= 24.0;
        }
        if (t < 10.0) {
            ans += "0";
        }

        String hour = String.format(Locale.getDefault(), "%.0f", Math.floor(t));
        ans += hour;
        ans +=":";

        t = (t * 60.0) - 60.0 * Math.floor(t);
        if (t < 10.0) {
            ans += "0";
        }

        String minute = String.format(Locale.getDefault(), "%.0f", Math.floor(t));
        ans += minute;
        ans += ":";

        t = (t * 60.0) - 60.0 * Math.floor(t);
        if (t < 10.0) {
            ans += "0";
        }

        String second = String.format(Locale.getDefault(), "%.0f", Math.floor(t));
        ans += second;
        ans += ".";

        String milisecond = String.format(Locale.getDefault(), "%.0f", Math.floor(10.0 * (t - Math.floor(t))));
        ans += milisecond;

        return ans;
    }


    // Display the information about 1st contact
    public String displayc1(){
        return  String.format(Locale.getDefault(), "C1: %s %s %s %s", getdate(c1), gettime(c1), getalt(c1), getazi(c1));
    }
    // Display the information about 2nd contact
    public String displayc2(){
        return  String.format(Locale.getDefault(), "C2: %s %s %s %s", getdate(c2), gettime(c2), getalt(c2), getazi(c2));
    }

    // Display the information about maximum eclipse
    public String displaymid(){
        return  String.format(Locale.getDefault(), "MID: %s %s %s %s", getdate(mid), gettime(mid), getalt(mid), getazi(mid));
    }

    //
    // Display the information about 3rd contact
    public String displayc3(){
        return  String.format(Locale.getDefault(), "C3: %s %s %s %s", getdate(c3), gettime(c3), getalt(c3), getazi(c3));
    }
    // Display the information about 4th contact
    public String displayc4(){
        return  String.format(Locale.getDefault(), "C4: %s %s %s %s", getdate(c4), gettime(c4), getalt(c4), getazi(c4));
    }


    // Get the altitude
    public String getalt(Double[] circumstances){
        String ans = "";
        Double t = circumstances[31] * R2D;
//        if abs(t) < 10.0 {
//            if t >= 0.0 {
//                ans += "0"
//            } else {
//                ans.append("-0")
//            }
//        }
        ans = ans.concat(String.format(Locale.getDefault(), "%.1f\u00B0", Math.abs(t)));
        return ans;
    }

    // Get the azimuth
    public String getazi(Double[] circumstances){
        String ans = "";
        Double t = circumstances[32] * R2D;

        if (t < 0.0) {
            t += 360.0;
        } else if (t >= 360.0) {
            t -= 360.0;
        }

//        if (t < 100.0) {
//            ans.append("0")
//        } else if (t < 10.0) {
//            ans.append("0")
//        }
        ans = ans.concat(String.format(Locale.getDefault(),  "%.1f\u00B0", t));

        return ans;
    }

    // Get the duration in 00m00.0s format
    public String getduration(){

        Double tmp = c3[1] - c2[1];
        if (tmp < 0.0) {
            tmp += 24.0;
        } else if (tmp >= 24.0) {
            tmp -= 24.0;
        }

        tmp = (tmp * 60.0) - 60.0 * Math.floor(tmp) + 0.05 / 60.0;
        String minutes = String.format(Locale.getDefault(), "%.0fm", Math.floor(tmp));

        String singleDigit = "";
        tmp = (tmp * 60.0) - 60.0 * Math.floor(tmp);
        if (tmp < 10.0) {
            singleDigit = "0";
        }

        String seconds = String.format(Locale.getDefault(), "%.0f.%.0fs", Math.floor(tmp), Math.floor((tmp - Math.floor(tmp)) * 10.0));

        return String.format(Locale.getDefault(), "%s %s %s", minutes, singleDigit, seconds );
    }

    // Get the obscuration
    public String getcoverage(){

        Double a = 0.0;
        Double b = 0.0;
        Double c = 0.0;

        if( mid[34] <= 0.0) {
            return "0.00%";
        } else if (mid[34] >= 1.0) {
            return "100.00%";
        }
        if (mid[36] == 2) {
            c = mid[35] * mid[35];
        } else {
            c = Math.acos((mid[28] * mid[28] + mid[29] * mid[29] - 2.0 * mid[33] * mid[33]) / (mid[28] * mid[28] - mid[29] * mid[29]));
            b = Math.acos((mid[28] * mid[29] + mid[33] * mid[33]) / mid[33] / (mid[28] + mid[29]));
            a = Math.PI - b - c;
            c = ((mid[35] * mid[35] * a + b) - mid[35] * Math.sin(c)) / Math.PI;
        }
        return String.format(Locale.getDefault(), "%.2f%%", c * 100);
    }


    // Compute the local circumstances
    public void loc_circ(Double lat, Double lon) {
        readdata(lat, lon);
        getall();
    }


    public void printTimes() {
        isPartial = false;
        isEclipse = true;

        String c1Display = "";
        String c2Display = "";
        String c3Display = "";
        String c4Display = "";

        String midDisplay = displaymid();
        if (mid[36] > 0) {
            // There is an eclipse
            c1Display = displayc1();
            c4Display = displayc4();
            if (mid[36] > 1) {
                // Total/annular eclipse
                c2Display = displayc2();
                c3Display = displayc3();
                if (c1[31] <= 0.0 && c4[31] <= 0.0){
                    // Sun below the horizon for the entire duration of the event
                    isEclipse = false;
                    System.out.print("No Solar Eclipse");
                } else {
                    // Sun above the horizon for at least some of the event
                    if (c2[31] <= 0.0 && c3[31] <= 0.0) {
                        // Sun below the horizon for just the total/annular event
                        isPartial = true;
                        System.out.print("Partial Solar Eclipse");
                    } else {
                        // Sun above the horizon for at least some of the total/annular event
                        if (c2[31] > 0.0 && c3[31] > 0.0) {
                            // Sun above the horizon for the entire annular/total event
                            if (mid[36] == 2) {
                                System.out.print("Annular Solar Eclipse" );
                                System.out.printf("Duration of Annularity %s: ", getduration());
                            } else {
                                System.out.print("Total Solar Eclipse" );
                                System.out.printf("Duration of Totality %s:", getduration());
                            }
                        } else {
                            // Sun below the horizon for at least some of the annular/total event
                            System.out.print("???");
                        }
                    }
                }
            } else {
                // Partial eclipse
                if (c1[31] <= 0.0 && c4[31] <= 0.0) {
                    // Sun below the horizon
                    isEclipse = false;
                    System.out.print("No Solar Eclipse");
                } else {
                    isPartial = true;
                    System.out.print("Partial Solar Eclipse");
                }
            }
        } else {
            // No eclipse
            isEclipse = false;
            System.out.print("No Solar Eclipse");
        }

        if (isEclipse) {
            Double maxmag = Math.round(mid[34] * 1000) / 1000.0;
            System.out.printf("Magnitude: %f", maxmag);
            System.out.printf("Obscuration: %s", getcoverage());
            System.out.println(c1Display);
            if (!isPartial) {
                System.out.print(c2Display);
            }
            System.out.print(midDisplay);
            if (!isPartial) {
                System.out.print(c3Display);

            }
            System.out.print(c4Display);
        }

        if (isEclipse) {
            if (isPartial) {
                type = EclipseType.PARTIAL;
            } else {
                type = EclipseType.FULL;
            }
        } else {
            type = EclipseType.NONE;
        }
    }


    public class EclipseEvent {

        public String name = "";
        public String date = "";
        public String time = "";
        public String alt = "";
        public String azi = "";

        public EclipseEvent(String name, String date, String time, String alt, String azi){
            this.name = name;
            this.date = date;
            this.time = time;
            this.alt = alt;
            this.azi = azi;
        }
    }


    public enum EclipseType {
        PARTIAL, FULL, NONE
    }
}

