package codeu.model.store.basic;

import java.util.Comparator;
import codeu.model.data.Message;

class SortByCreationTime implements Comparator<Message>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Message a, Message b)
    {
	boolean comp = a.getCreationTime().isAfter(b.getCreationTime());
        if(comp)
	    return -1;
	return 1;
    }
}

