package ut.ee.workflow.object;

public class PartnerLink {
	public String name;
	public String partnerLinkType;
	public String myRole;
	public PartnerLink(String _name, String _partnerLinkType, String _myRole){
		name = _name;
		partnerLinkType = _partnerLinkType;
		myRole = _myRole;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  name + "   " + partnerLinkType + "   " + myRole + "\n";
	}
	
}


//For example, you will have a class called "PartnerLink" for the
//<partnerLink> tag, which consists of three attributes: name,
//partnerLinkType and myRole. All of them are String.