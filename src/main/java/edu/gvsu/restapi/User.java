package edu.gvsu.restapi;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.json.JSONObject;

/**
 * A POJO representing the user resource.
 */

@Entity
public class User {

    @Id
    String name = null;
    String ipAddress = null;
    String port = null;
    boolean status = true;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress The name to set.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Convert this object to a JSON object for representation
     */
    public JSONObject toJSON() {
        try {
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name", this.name);
            jsonobj.put("ipAddress", this.ipAddress);
            jsonobj.put("port", this.port);
            jsonobj.put("status", this.status);
            return jsonobj;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert this object to a string for representation
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("name:");
        sb.append(this.name);
        sb.append(",ipAddress:");
        sb.append(this.ipAddress);
        sb.append(",port:");
        sb.append(this.port);
        sb.append(",status:");
        sb.append(this.status);
        return sb.toString();
    }

    /**
     * Convert this object into an HTML representation.
     *
     * @param fragment if true, generate an html fragment, otherwise a complete document.
     * @return an HTML representation.
     */
    public String toHtml(boolean fragment) {
        String retval = "";
        if (fragment) {
            StringBuffer sb = new StringBuffer();
            sb.append("<h3 style=\"border:2px solid Violet;\">");
            sb.append("<b> Name: </b>");
            sb.append(this.name);
            sb.append("<br></br>");
            sb.append("<b>IP Address: </b>");
            sb.append(this.ipAddress);
            sb.append(" <a href=\"/users/" + this.name + "\">View</a>");
            sb.append("<br/>");
            sb.append("</h3>");
            retval = sb.toString();
        } else {
            StringBuffer sb = new StringBuffer("<html><head><title>User</title></head><body><h1>User Representation</h1>");
            sb.append("<br/><b>Name: </b>");
            sb.append(this.name);
            sb.append("<br></br>");
            sb.append("<b>ipAddress: </b>");
            sb.append(this.ipAddress);
            sb.append("<br/><br/>Return to <a href=\"/users\">user list<a>.</body></html>");
            retval = sb.toString();
        }
        return retval;
    }
}

