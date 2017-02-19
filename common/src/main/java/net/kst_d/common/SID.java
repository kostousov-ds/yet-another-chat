package net.kst_d.common;

public class SID {
    public static final SID NONE = new SID("--none--");

    private final String sid;

    public SID(String sid) {
	this.sid = sid;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (!(o instanceof SID)) {
	    return false;
	}

	SID sid1 = (SID) o;

	return sid.equals(sid1.sid);
    }

    @Override
    public int hashCode() {
	return sid.hashCode();
    }

    @Override
    public String toString() {
	return sid;
    }
}
