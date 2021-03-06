package org.urlMonitor.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;

import org.urlMonitor.model.type.StatusType;
import org.urlMonitor.util.DateUtil;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class MonitorInfo implements Serializable {
    private int hashCode;
    private StatusType status;
    private String lastChecked;
    private String lastFailed;

    public MonitorInfo(int hashCode, StatusType status, Date lastChecked,
            Date lastFailed) {
        this.hashCode = hashCode;
        this.status = status;
        Date now = new Date();
        if (lastChecked != null) {
            this.lastChecked =
                    DateUtil.getHowLongAgoDescription(lastChecked, now);
        }
        if (lastFailed != null) {
            this.lastFailed =
                    DateUtil.getHowLongAgoDescription(lastFailed, now);
        }
    }
}
