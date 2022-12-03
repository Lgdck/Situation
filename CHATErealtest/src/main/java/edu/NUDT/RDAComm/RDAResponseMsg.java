package edu.NUDT.RDAComm;

import edu.harvard.syrah.sbon.async.comm.obj.ObjMessage;
import util.bloom.CHTE.RDATableEntry;

public class RDAResponseMsg extends ObjMessage {
	static final long serialVersionUID = 19L;

	public RDATableEntry[] _hashTable;
	
	public RDAResponseMsg(RDATableEntry[] hashTable) {
				
		_hashTable=hashTable;	
	}
	
	public RDAResponseMsg(){
		
	}
}