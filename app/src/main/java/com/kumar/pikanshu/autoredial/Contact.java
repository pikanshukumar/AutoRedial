package com.kumar.pikanshu.autoredial;

/**
 * Created by pika on 24/10/16.
 * This class represents a contact item from Contact list of the user.
 * It contains a name and number for that name.
 */

public class Contact  {

    /** Name of contact */
    private String mName;

    /** Phone number for that name */
    private String mPhoneNumber;

    /** selected for redial by user */
    private boolean selected = false;

    public Contact(String name, String phoneNumber) {
        mName = name;
        mPhoneNumber = phoneNumber;
    }

    /** Get the name  */

    public String getName(){
        return mName;
    }

    /** Get the PhoneNumber  */

    public String getPhoneNumber(){
        return mPhoneNumber;
    }


    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        boolean equalFlag = false;

        if(o instanceof Contact){
            if(this.mName.equals(((Contact) o).mName)){
                String s1 = this.mPhoneNumber.replaceAll(" ","").replaceAll("-", "");
                String s2 = ((Contact) o).mPhoneNumber.replaceAll(" ","").replaceAll("-", "");
                if(s1.equals(s2)){
                    equalFlag = true;
                }
            }
        }

        return equalFlag;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((mPhoneNumber == null) ? 0 : mPhoneNumber.hashCode());
        return result;
    }
}
