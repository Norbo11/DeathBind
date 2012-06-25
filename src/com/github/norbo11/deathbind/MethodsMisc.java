package com.github.norbo11.deathbind;

public class MethodsMisc
{
    public MethodsMisc(DeathBind p)
    {
        this.p = p;
    }
    DeathBind p;
    
    public boolean isInteger(String toCheck)
    {
        try {
            Integer.parseInt(toCheck); 
            return true;
        } catch (Exception e) { return false; }
    }
}
