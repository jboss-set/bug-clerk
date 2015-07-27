package org.jboss.jbossset.bugclerk.utils;

import org.jboss.pull.shared.connectors.common.Flag;

public final class FlagsUtils {

    public static final String DEV_ACK_FLAG = "devel_ack";
    public static final String QA_ACK_FLAG = "qa_ack";
    public static final String PM_ACK_FLAG = "pm_ack";

    private FlagsUtils(){}

    private static final String DEV = "dev";
    public static final String RELEASE_64Z = "jboss‑eap‑6.4.z";

    public static String formatAckFlagname(Flag ackFlag) {
        if ( ackFlag == null )
            return " - ";

        StringBuffer res;
        switch (ackFlag.getName()) {
            case DEV_ACK_FLAG:
                res = new StringBuffer(DEV.length()).append(DEV);
                break;
             default:
                res = new StringBuffer(2).append(ackFlag.getName().substring(0, 2));
                break;
        }
        return res.toString();
    }

    public static String formatAckFlagWithStatus(Flag ackFlag) {
        String flagname = formatAckFlagname(ackFlag);
        String status = formatAck(ackFlag);
        return flagname + status;
    }

    public static String formatAck(Flag ackFlag){
        StringBuffer res= new StringBuffer();
        if ( ackFlag == null || ackFlag.getStatus() == null )
            return "";
        switch(ackFlag.getStatus()){
            case NEGATIVE:
                res.append("-");
                break;
            case POSITIVE:
                res.append("+");
                break;
            default:
                res.append("?");
                break;
        }

        return res.toString();
    }

}
