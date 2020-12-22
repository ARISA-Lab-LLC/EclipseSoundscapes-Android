package org.eclipsesoundscapes.data;

import android.content.Context;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.model.Event;
import org.joda.time.DateTime;

import java.util.Locale;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 * */


/**
 * @author Joel Goncalves
 *
 * Generate eclipse events based on latitude, longitude
 */

public class EclipseTimeGenerator {
    public enum EclipseType {
        PARTIAL, FULL, NONE
    }

    public EclipseType type = EclipseType.FULL;

    private Context context;
    private Double longitude;
    private Double latitude;

    private double R2D = 180.0 / Math.PI;
    private double D2R = Math.PI / 180;

    private Double[] obsvconst = new Double[10];
    private Double[] elements = {2458667.308420,
            19.0,
            -4.0,
            4.0,
            69.0,
            -0.21563400,
            0.56620699,
            0.00002737,
            -0.00000880,
            -0.65070909,
            0.01064008,
            -0.00012723,
            -0.00000026,
            23.01294899,
            -0.00318700,
            -0.00000549,
            103.97974396,
            14.99950695,
            0.00000104,
            0.53764975,
            -0.00008983,
            -0.00001204,
            -0.00846503,
            -0.00008938,
            -0.00001198,
            0.00459839,
            0.00457549};

    private Double[] c1 = new Double[40];
    private Double[] c2 = new Double[40];
    private Double[] mid = new Double[40];
    private Double[] c3 = new Double[40];
    private Double[] c4 = new Double[40];

    public EclipseTimeGenerator(Context context, Double latitude, Double longitude) {
        this.context = context;
        this.longitude = longitude;
        this.latitude = latitude;
        loc_circ(latitude, longitude);
        printTimes();
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getMagnitude() {
        if (type == EclipseType.NONE)
            return null;
        return String.valueOf(Math.round(mid[34] * 1000) / 1000);
    }

    public String getFormattedDuration() {
        if (type == EclipseType.FULL) {
            final DateTime dateTime = getDuration();
            return String.format(Locale.getDefault(), "%dm %d.%ds",
                    dateTime.getMinuteOfHour(),
                    dateTime.getSecondOfMinute(),
                    dateTime.getMillisOfSecond());
        }
        return "";
    }

    public Event contact1() {
        return new Event(context.getString(R.string.eclipse_start_partial), getDate(c1), getTime(c1), getAlt(c1), getAzi(c1));
    }

    public Event contact2() {
        return new Event(context.getString(R.string.eclipse_start_full), getDate(c2), getTime(c2), getAlt(c2), getAzi(c2));
    }

    public Event contactMid() {
        return new Event(context.getString(R.string.eclipse_max), getDate(mid), getTime(mid), getAlt(mid), getAzi(mid));
    }

    public Event contact3() {
        return new Event(context.getString(R.string.eclipse_end_full), getDate(c3), getTime(c3), getAlt(c3), getAzi(c3));
    }

    public Event contact4() {
        return new Event(context.getString(R.string.eclipse_end_partial), getDate(c4), getTime(c4), getAlt(c4), getAzi(c4));
    }


    // Populate the circumstances array with the time-only dependent circumstances (x, y, d, m, ...)
    private Double[] timeDependent(Double[] circumstances) {
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
    private Double[] timeLocalDependent(Double[] circumstances) {

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

    private Double[] c1c4iterate(Double[] circumstances) {
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
    private void getc1c4() {
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
    private Double[] c2c3iterate(Double[] circumstances) {
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
    private void getc2c3() {
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
    private Double[] observational(Double[] circumstances) {

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
    private void getMid() {
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
    private void getAll() {
        getMid();
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
                // Partial eclipse
                mid[36] = 1.0;
            }
            c1 = observational(c1);
            c4 = observational(c4);
        } else {
            // No eclipse
            mid[36] = 0.0;
        }
    }

    // Read the data, and populate the obsvconst array
    private void readData(Double lat, Double lon) {
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

    // Utilized in getday()
    // Pads digits
    public String padDigits(Double n, int totalDigits){
        String nString = String.format(Locale.getDefault(), "%.0f", n);

        StringBuilder pd = new StringBuilder();
        if (totalDigits > nString.length()) {
            for (int i = 0; i < totalDigits - nString.length(); i++){
                pd.append("0");
            }
        }
        return  String.format(Locale.getDefault(), "%s%s", pd.toString(), nString);
    }

    // Get the local date
    private String getDate(Double[] circumstances){

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
    private String getTime(Double[] circumstances){
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
    private String displayC1(){
        return  String.format(Locale.getDefault(), "C1: %s %s %s %s", getDate(c1), getTime(c1), getAlt(c1), getAzi(c1));
    }

    // Display the information about 2nd contact
    private String displayC2(){
        return  String.format(Locale.getDefault(), "C2: %s %s %s %s", getDate(c2), getTime(c2), getAlt(c2), getAzi(c2));
    }

    // Display the information about maximum eclipse
    private String displayMid(){
        return  String.format(Locale.getDefault(), "MID: %s %s %s %s", getDate(mid), getTime(mid), getAlt(mid), getAzi(mid));
    }

    // Display the information about 3rd contact
    private String displayC3(){
        return  String.format(Locale.getDefault(), "C3: %s %s %s %s", getDate(c3), getTime(c3), getAlt(c3), getAzi(c3));
    }

    // Display the information about 4th contact
    private String displayC4(){
        return  String.format(Locale.getDefault(), "C4: %s %s %s %s", getDate(c4), getTime(c4), getAlt(c4), getAzi(c4));
    }


    // Get the altitude
    private String getAlt(Double[] circumstances){
        Double t = circumstances[31] * R2D;
        return String.format(Locale.getDefault(), "%.1f\u00B0", Math.abs(t));
    }

    // Get the azimuth
    private String getAzi(Double[] circumstances){
        Double t = circumstances[32] * R2D;

        if (t < 0.0) {
            t += 360.0;
        } else if (t >= 360.0) {
            t -= 360.0;
        }

        return  String.format(Locale.getDefault(),  "%.1f\u00B0", t);
    }

    public DateTime getDuration(){

        Double tmp = c3[1] - c2[1];
        if (tmp < 0.0) {
            tmp += 24.0;
        } else if (tmp >= 24.0) {
            tmp -= 24.0;
        }

        tmp = (tmp * 60.0) - 60.0 * Math.floor(tmp) + 0.05 / 60.0;
        final int minutes = (int) Math.floor(tmp);

        tmp = (tmp * 60.0) - 60.0 * Math.floor(tmp);
        final int seconds = (int) Math.floor(tmp);
        final int milliseconds = (int) Math.floor((tmp - Math.floor(tmp)) * 10.0);

        return new DateTime()
                .withMinuteOfHour(minutes)
                .withSecondOfMinute(seconds)
                .withMillisOfSecond(milliseconds);
    }

    // Get the obscuration
    public String getCoverage(){
        Double a;
        Double b;
        Double c;

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
    private void loc_circ(Double lat, Double lon) {
        readData(lat, lon);
        getAll();
    }


    private void printTimes() {
        boolean isPartial = false;
        boolean isEclipse = true;

        if (mid[36] > 0) {
            // There is an eclipse
            if (mid[36] > 1) {
                // Total/annular eclipse
                if (c1[31] <= 0.0 && c4[31] <= 0.0){
                    // Sun below the horizon for the entire duration of the event
                    isEclipse = false;
                } else {
                    // Sun above the horizon for at least some of the event
                    if (c2[31] <= 0.0 && c3[31] <= 0.0) {
                        // Sun below the horizon for just the total/annular event
                        isPartial = true;
                    } else {
                        // Sun above the horizon for at least some of the total/annular event
                        if (c2[31] > 0.0 && c3[31] > 0.0) {
                            // Sun above the horizon for the entire annular/total event
                            if (mid[36] == 2) {
                                // Annular Solar Eclipse
                            } else {
                                // Total solar Eclipse
                            }
                        } else {
                            // Sun below the horizon for at least some of the annular/total event
                        }
                    }
                }
            } else {
                // Partial eclipse
                if (c1[31] <= 0.0 && c4[31] <= 0.0) {
                    // Sun below the horizon
                    isEclipse = false;
                } else {
                    isPartial = true;
                }
            }
        } else {
            // No eclipse
            isEclipse = false;
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
}

