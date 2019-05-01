package net.nawaman.pattern;

import java.io.File;
import java.io.FileNotFoundException;

import net.nawaman.curry.Engine;
import net.nawaman.curry.MUnit;
import net.nawaman.curry.Scope;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.script.Executable;
import net.nawaman.script.Function;
import net.nawaman.script.Macro;
import net.nawaman.script.Script;
import net.nawaman.script.Signature;
import net.nawaman.script.Tools;

public class Main {

	static public final String EVARNAME_ProjectName        = "ProjectName";
	static public final String EVARNAME_ScriptName         = "ScriptName";
	static public final String EVARNAME_ScriptFileName     = "ScriptFileName";
	static public final String EVARNAME_ScriptFileFullName = "ScriptFileFullName";
	
	static public final String FileExtension       = ".project"; 
	
    static public void main(String ... $Args) {
		
		//$Args = new String[] { "Test.project", "", "param", "" };
		//$Args = new String[] { "test.intention", "", "param", "" };
		
		if(($Args == null) || ($Args.length == 0)) {
			System.out.println("Please tell me something ...\n");
			return;
		}
		
		String   ScriptName  = $Args[0];
		Object[] ScriptParams = new Object[$Args.length - 1];
		for(int i = 0; i < ScriptParams.length; i++) ScriptParams[i] = $Args[i + 1];
		
		Executable Exec       = null;
		File       ScriptFile = null;
		if(!((ScriptFile = new File(ScriptName))).exists()) {
			if(((ScriptFile = new File(ScriptName + FileExtension))).exists())
				ScriptName = ScriptName + FileExtension;
			else {
				// Simple script file
				try { Exec = Tools.Use(ScriptName); }
				catch (FileNotFoundException E) {}
				catch (Exception E) { throw new RuntimeException(E); }
				
				if(Exec == null) {
					System.out.println("The script file does not exist.");
					return;
				}
			}
		}

		if(Exec == null) {
			Engine.IsToVocal = false;
			CurryLanguage $CLanguage = PatternEngine.Instance.getCurryLanguage();
			Engine        $Engine    = $CLanguage.getTargetEngine();
			MUnit         $Units     = $Engine.getUnitManager();
			
			$Units.discoverUsepaths();
			
			File F = (ScriptFile = new File(ScriptName));
			
			try { Exec = Tools.Use(F, false); }
			catch (Exception E) { throw new RuntimeException(E); }
		}

		// Set the project name
		Engine $Engine     = PatternEngine.Instance.getCurryLanguage().getTargetEngine();
		Scope  EngineScope = $Engine.getEngineScope();
		
		String FName = ScriptFile.getName();
		String Name  = FName;
		if(FName.endsWith(FileExtension))
			Name = Name.substring(0, Name.length() - FileExtension.length());
		
		EngineScope.newConstant($Engine, EVARNAME_ProjectName,        TKJava.TString, Name);
		EngineScope.newConstant($Engine, EVARNAME_ScriptName,         TKJava.TString, ScriptName);
		EngineScope.newConstant($Engine, EVARNAME_ScriptFileName,     TKJava.TString, FName);
		EngineScope.newConstant($Engine, EVARNAME_ScriptFileFullName, TKJava.TString, ScriptFile.getAbsolutePath());
		
		
		if(Exec instanceof Script) {
			((Script)Exec).run();
			return;
		}
		if(Exec instanceof Macro) {
			Signature Signature = ((Macro)Exec).getSignature();
			Object[]  Params    = net.nawaman.script.Signature.Simple.adjustParameters(Signature, (Object[])ScriptParams);
			Object    Result    = ((Macro)Exec).run((Object[])Params);
			
			if(Signature.getReturnType() != Void.class)
				System.out.println(Result);

			return;
		}
		if(Exec instanceof Function) {
			Signature Signature = ((Function)Exec).getSignature();
			Object[]  Params    = net.nawaman.script.Signature.Simple.adjustParameters(Signature, (Object[])ScriptParams);
			Object    Result    = ((Function)Exec).run((Object[])Params);
			
			if(Signature.getReturnType() != Void.class)
				System.out.println(Result);
			
			return;
		}
		
		throw new RuntimeException("Unknown script.");
	}
	
}
