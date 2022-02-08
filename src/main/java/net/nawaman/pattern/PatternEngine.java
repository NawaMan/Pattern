package net.nawaman.pattern;

import java.io.File;

import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.curry.script.CurryEngine;
import net.nawaman.script.ExecutableInfo;
import net.nawaman.script.Signature;

public class PatternEngine extends CurryEngine {

	static public final String EngineName = "Pattern";
	
	static public final PatternEngine Instance = new PatternEngine();
	
	static { CurryEngine.registerCurryEngine(Instance); }
	
	public PatternEngine() {
		this(CurryLanguage.Util.GetGetCurryLanguage(EngineName).getCurryLanguage(null, null));
	}
	
	/** Constructs a curry engine */
	private PatternEngine(CurryLanguage pCLanguage) {
		super(EngineName, null, pCLanguage.getTargetEngine(), pCLanguage);
	}

	/** Returns the curry language of this engine */
	public CurryLanguage getCurryLanguage() {
		return ((EE_Language)this.getTheEngine().getExtension(EE_Language.Name)).getDefaultLanguage();
	}
	
	/**{@inheritDoc}*/@Override
	public String getName() {
		return EngineName;
	}
	
	/**{@inheritDoc}*/ @Override
	public ExecutableInfo getReplaceExecutableInfo(ExecutableInfo EInfo) {
		if(EInfo == null) return null;
		
		if(EInfo.Kind == null){
			String FName = EInfo.FileName;
			if((FName != null) && FName.endsWith(".intention")) {
				String SName = FName.replace(".", "_").replace("" + File.separatorChar, "$");
				EInfo = new ExecutableInfo(
					FName,
					"macro",
					new Signature.Simple(SName, Object.class, true, Object.class),
					SName + "($Args:Object ...):Void ",
					new String[] { "$Args" }
				);
			}
		}
		return EInfo;
	}
}
