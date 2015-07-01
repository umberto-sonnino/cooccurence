package it.uniroma1.lcl.wimmp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for Homework 1 - NLP 2014
 * 
 * @author flati, navigli
 *
 */
public class Test
{
	public static void main(String[] args) throws Exception
	{
		if (args.length < 3 || args.length > 4)
		{
			System.out.println("Usage: java it.uniroma1.lcl.wimmp.Test <language> <lang_code> <dump_file1> [<dump_file2>]");
			System.exit(1);
		}
		
		String lang = args[0];
		String langCode = args[1];
		String[] dumps = Arrays.copyOfRange(args, 2, args.length);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("morpho_"+langCode+".txt")));
		
		Class<?> c = Class.forName("it.uniroma1.lcl.wimmp."+lang+"MorphoEntryIterator");
		Class<? extends MorphoEntryIterator> m = c.asSubclass(MorphoEntryIterator.class);
		
		Constructor<? extends MorphoEntryIterator> constr = m.getConstructor(String[].class);
		MorphoEntryIterator iterator = constr.newInstance(new Object[] { dumps });
		
		while(iterator.hasNext())
		{
			MorphoEntry morphologicalEntry = iterator.next();
			List<MorphoForm> forms = morphologicalEntry.getRule().getForms();
			
			writer.write(morphologicalEntry.getLemma() + "#" + morphologicalEntry.getPOS());
			writer.write("\t" + morphologicalEntry.isRegular());
			
			for (MorphoForm morphologicalForm : forms)
				writer.write("\t" + morphologicalForm.getForm() + "\t" + morphologicalForm.getInfo());
			
			writer.newLine();
		}
		writer.close();
	}
}
