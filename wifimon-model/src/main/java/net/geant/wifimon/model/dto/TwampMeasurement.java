package net.geant.wifimon.model.dto;

import java.io.Serializable;

/**
 * Created by nkostopoulos on 03/22/2022
 */

public class TwampMeasurement implements Serializable {
   private String probeNumber;
   private String twampServer;
   private String sent;
   private String lost;
   private String sendDups;
   private String reflectDups;
   private String minRtt;
   private String medianRtt;
   private String maxRtt;
   private String errRtt;
   private String minSend;
   private String medianSend;
   private String maxSend;
   private String errSend;
   private String minReflect;
   private String medianReflect;
   private String maxReflect;
   private String errReflect;
   private String minReflectorProcessingTime;
   private String maxReflectorProcessingTime;
   private String twoWayJitterValue;
   private String twoWayJitterChar;
   private String sendJitterValue;
   private String sendJitterChar;
   private String reflectJitterValue;
   private String reflectJitterChar;
   private String sendHopsValue;
   private String sendHopsChar;
   private String reflectHopsValue;
   private String reflectHopsChar;

   public String getProbeNumber() {
       return probeNumber;
   }

   public void setProbeNumber(String probeNumber) {
       this.probeNumber = probeNumber;
   }

   public String getTwampServer() {
       return twampServer;
   }

   public void setTwampServer(String twampServer) {
       this.twampServer = twampServer;
   }

   public String getSent() {
       return sent;
   }

   public void setSent(String sent) {
       this.sent = sent;
   }

   public String getLost() {
       return lost;
   }

   public void setLost(String lost) {
       this.lost = lost;
   }

   public String getSendDups() {
       return sendDups;
   }

   public void setSendDups(String sendDups) {
       this.sendDups = sendDups;
   }

   public String getReflectDups() {
       return reflectDups;
   }

   public void setReflectDups(String reflectDups) {
       this.reflectDups = reflectDups;
   }

   public String getMinRtt() {
       return minRtt;
   }

   public void setMinRtt(String minRtt) {
       this.minRtt = minRtt;
   }

   public String getMedianRtt() {
       return medianRtt;
   }

   public void setMedianRtt(String medianRtt) {
       this.medianRtt = medianRtt;
   }

   public String getMaxRtt() {
       return maxRtt;
   }

   public void setMaxRtt(String maxRtt) {
       this.maxRtt = maxRtt;
   }

   public String getErrRtt() {
       return errRtt;
   }

   public void setErrRtt(String errRtt) {
       this.errRtt = errRtt;
   }

   public String getMinSend() {
       return minSend;
   }

   public void setMinSend(String minSend) {
       this.minSend = minSend;
   }

   public String getMedianSend() {
       return medianSend;
   }

   public void setMedianSend(String medianSend) {
       this.medianSend = medianSend;
   }

   public String getMaxSend() {
       return maxSend;
   }

   public void setMaxSend(String maxSend) {
       this.maxSend = maxSend;
   }

   public String getErrSend() {
       return errSend;
   }

   public void setErrSend(String errSend) {
       this.errSend = errSend;
   }

   public String getMinReflect() {
       return minReflect;
   }

   public void setMinReflect(String minReflect) {
       this.minReflect = minReflect;
   }

   public String getMedianReflect() {
       return medianReflect;
   }

   public void setMedianReflect(String medianReflect) {
       this.medianReflect = medianReflect;
   }

   public String getMaxReflect() {
       return maxReflect;
   }

   public void setMaxReflect(String maxReflect) {
       this.maxReflect = maxReflect;
   }

   public String getErrReflect() {
       return errReflect;
   }

   public void setErrReflect(String errReflect) {
       this.errReflect = errReflect;
   }

   public String getMinReflectorProcessingTime() {
       return minReflectorProcessingTime;
   }

   public void setMinReflectorProcessingTime(String minReflectorProcessingTime) {
       this.minReflectorProcessingTime = minReflectorProcessingTime;
   }

   public String getMaxReflectorProcessingTime() {
       return maxReflectorProcessingTime;
   }

   public void setMaxReflectorProcessingTime(String maxReflectorProcessingTime) {
       this.maxReflectorProcessingTime = maxReflectorProcessingTime;
   }

   public String getTwoWayJitterValue() {
       return twoWayJitterValue;
   }

   public void setTwoWayJitterValue(String twoWayJitterValue) {
       this.twoWayJitterValue = twoWayJitterValue;
   }

   public String getTwoWayJitterChar() {
       return twoWayJitterChar;
   }

   public void setTwoWayJitterChar(String twoWayJitterChar) {
       this.twoWayJitterChar = twoWayJitterChar;
   }

   public String getSendJitterValue() {
       return sendJitterValue;
   }

   public void setSendJitterValue(String sendJitterValue) {
       this.sendJitterValue = sendJitterValue;
   }

   public String getSendJitterChar() {
       return sendJitterChar;
   }

   public void setSendJitterChar(String sendJitterChar) {
       this.sendJitterChar = sendJitterChar;
   }

   public String getReflectJitterValue() {
       return reflectJitterValue;
   }

   public void setReflectJitterValue(String reflectJitterValue) {
       this.reflectJitterValue = reflectJitterValue;
   }

   public String getReflectJitterChar() {
       return reflectJitterChar;
   }

   public void setReflectJitterChar(String reflectJitterChar) {
       this.reflectJitterChar = reflectJitterChar;
   }

   public String getSendHopsValue() {
       return sendHopsValue;
   }

   public void setSendHopsValue(String sendHopsValue) {
       this.sendHopsValue = sendHopsValue;
   }

   public String getSendHopsChar() {
       return sendHopsChar;
   }

   public void setSendHopsChar(String sendHopsChar) {
       this.sendHopsChar = sendHopsChar;
   }

   public String getReflectHopsValue() {
       return reflectHopsValue;
   }

   public void setReflectHopsValue(String reflectHopsValue) {
       this.reflectHopsValue = reflectHopsValue;
   }

   public String getReflectHopsChar() {
       return reflectHopsChar;
   }

   public void setReflectHopsChar(String reflectHopsChar) {
       this.reflectHopsChar = reflectHopsChar;
   }

   @Override
   public boolean equals(Object o) {
       if (this == o)
           return true;
       if (o == null || getClass() != o.getClass())
	   return false;

       TwampMeasurement that = (TwampMeasurement) o;

       if (probeNumber != null ? !probeNumber.equals(that.probeNumber) : that.probeNumber != null)
           return false;
       if (twampServer != null ? !twampServer.equals(that.twampServer) : that.twampServer != null)
           return false;
       if (sent != null ? !sent.equals(that.sent) : that.sent != null)
           return false;
       if (lost != null ? !lost.equals(that.lost) : that.lost != null)
           return false;
       if (sendDups != null ? !sendDups.equals(that.sendDups) : that.sendDups != null)
           return false;
       if (reflectDups != null ? !reflectDups.equals(that.reflectDups) : that.reflectDups != null)
           return false;
       if (minRtt != null ? !minRtt.equals(that.minRtt) : that.minRtt != null)
           return false;
       if (medianRtt != null ? !medianRtt.equals(that.medianRtt) : that.medianRtt != null)
           return false;
       if (maxRtt != null ? !maxRtt.equals(that.maxRtt) : that.maxRtt != null)
           return false;
       if (errRtt != null ? !errRtt.equals(that.errRtt) : that.errRtt != null)
           return false;
       if (minSend != null ? !minSend.equals(that.minSend) : that.minSend != null)
           return false;
       if (medianSend != null ? !medianSend.equals(that.medianSend) : that.medianSend != null)
           return false;
       if (maxSend != null ? !maxSend.equals(that.maxSend) : that.maxSend != null)
           return false;
       if (errSend != null ? !errSend.equals(that.errSend) : that.errSend != null)
           return false;
       if (minReflect != null ? !minReflect.equals(that.minReflect) : that.minReflect != null)
           return false;
       if (medianReflect != null ? !medianReflect.equals(that.medianReflect) : that.medianReflect != null)
           return false;
       if (maxReflect != null ? !maxReflect.equals(that.maxReflect) : that.maxReflect != null)
           return false;
       if (errReflect != null ? !errReflect.equals(that.errReflect) : that.errReflect != null)
           return false;
       if (minReflectorProcessingTime != null ? !minReflectorProcessingTime.equals(that.minReflectorProcessingTime) : that.minReflectorProcessingTime != null)
           return false;
       if (maxReflectorProcessingTime != null ? !maxReflectorProcessingTime.equals(that.maxReflectorProcessingTime) : that.maxReflectorProcessingTime != null)
           return false;
       if (twoWayJitterValue != null ? !twoWayJitterValue.equals(that.twoWayJitterValue) : that.twoWayJitterValue != null)
           return false;
       if (twoWayJitterChar != null ? !twoWayJitterChar.equals(that.twoWayJitterChar) : that.twoWayJitterChar != null)
           return false;
       if (sendJitterValue != null ? !sendJitterValue.equals(that.sendJitterValue) : that.sendJitterValue != null)
           return false;
       if (sendJitterChar != null ? !sendJitterChar.equals(that.sendJitterChar) : that.sendJitterChar != null)
           return false;
       if (reflectJitterValue != null ? !reflectJitterValue.equals(that.reflectJitterValue) : that.reflectJitterValue != null)
           return false;
       if (reflectJitterChar != null ? !reflectJitterChar.equals(that.reflectJitterChar) : that.reflectJitterChar != null)
           return false;
       if (sendHopsValue != null ? !sendHopsValue.equals(that.sendHopsValue) : that.sendHopsValue != null)
           return false;
       if (sendHopsChar != null ? !sendHopsChar.equals(that.sendHopsChar) : that.sendHopsChar != null)
           return false;
       if (reflectHopsValue != null ? !reflectHopsValue.equals(that.reflectHopsValue) : that.reflectHopsValue != null)
           return false;
       return reflectHopsChar != null ? reflectHopsChar.equals(that.reflectHopsChar) : that.reflectHopsChar == null;
   }

   @Override
   public int hashCode() {
       int result = probeNumber != null ? probeNumber.hashCode() : 0;
       result = 31 * result + (twampServer != null ? twampServer.hashCode() : 0);
       result = 31 * result + (sent != null ? sent.hashCode() : 0);
       result = 31 * result + (lost != null ? lost.hashCode() : 0);
       result = 31 * result + (sendDups != null ? sendDups.hashCode() : 0);
       result = 31 * result + (reflectDups != null ? reflectDups.hashCode() : 0);
       result = 31 * result + (minRtt != null ? minRtt.hashCode() : 0);
       result = 31 * result + (medianRtt != null ? medianRtt.hashCode() : 0);
       result = 31 * result + (maxRtt != null ? maxRtt.hashCode() : 0);
       result = 31 * result + (errRtt != null ? errRtt.hashCode() : 0);
       result = 31 * result + (minSend != null ? minSend.hashCode() : 0);
       result = 31 * result + (medianSend != null ? medianSend.hashCode() : 0);
       result = 31 * result + (maxSend != null ? maxSend.hashCode() : 0);
       result = 31 * result + (errSend != null ? errSend.hashCode() : 0);
       result = 31 * result + (minReflect != null ? minReflect.hashCode() : 0);
       result = 31 * result + (medianReflect != null ? medianReflect.hashCode() : 0);
       result = 31 * result + (maxReflect != null ? maxReflect.hashCode() : 0);
       result = 31 * result + (errReflect != null ? errReflect.hashCode() : 0);
       result = 31 * result + (minReflectorProcessingTime != null ? minReflectorProcessingTime.hashCode() : 0);
       result = 31 * result + (maxReflectorProcessingTime != null ? maxReflectorProcessingTime.hashCode() : 0);
       result = 31 * result + (twoWayJitterValue != null ? twoWayJitterValue.hashCode() : 0);
       result = 31 * result + (twoWayJitterChar != null ? twoWayJitterChar.hashCode() : 0);
       result = 31 * result + (sendJitterValue != null ? sendJitterValue.hashCode() : 0);
       result = 31 * result + (sendJitterChar != null ? sendJitterChar.hashCode() : 0);
       result = 31 * result + (reflectJitterValue != null ? reflectJitterValue.hashCode() : 0);
       result = 31 * result + (reflectJitterChar != null ? reflectJitterChar.hashCode() : 0);
       result = 31 * result + (sendHopsValue != null ? sendHopsValue.hashCode() : 0);
       result = 31 * result + (sendHopsChar != null ? sendHopsChar.hashCode() : 0);
       result = 31 * result + (reflectHopsValue != null ? reflectHopsValue.hashCode() : 0);
       result = 31 * result + (reflectHopsChar != null ? reflectHopsChar.hashCode() : 0);
       return result;
   }
}
