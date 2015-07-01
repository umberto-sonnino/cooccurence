package it.uniroma1.lcl.wimmp;

public class MorphoForm
{
	private String form;
	private String info;
	
	public MorphoForm(String form)
	{
		this(form, "No-Info");
	}
	
	public MorphoForm(String form, String info)
	{
		this.form = form;
		this.info = info;
	}
	
	public String getForm(){ return form; }
	public String getInfo(){ return info; }

}
