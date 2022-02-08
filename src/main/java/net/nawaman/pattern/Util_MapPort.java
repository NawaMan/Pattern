package net.nawaman.pattern;

import net.nawaman.curry.Engine;
import net.nawaman.curry.Expression;
import net.nawaman.curry.Instructions_DataHolder.Inst_GetDHValue;
import net.nawaman.curry.Instructions_StackOwner.Inst_GetAttrValue;
import net.nawaman.curry.MExecutable;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.compiler.CompileProduct;
import net.nawaman.pattern.Instructions_Pattern.Inst_GetAttrMapPortElement;
import net.nawaman.pattern.Instructions_Pattern.Inst_GetDHValueOrNull;
import net.nawaman.pattern.Instructions_Pattern.Inst_GetMapPortElement;
import net.nawaman.pattern.Instructions_Pattern.Inst_IsMapPortContainsKey;
import net.nawaman.regparser.result.Coordinate;
import net.nawaman.regparser.result.ParseResult;
import net.nawaman.regparser.typepackage.PTypePackage;

public class Util_MapPort {
    
    /** Compile a NewVar Statement */
    static public Expression ParseCompileMapPortAccess(
            final String         OperandName,
            final String         KeyName,
            final String         AssociateBeginName,
            final String         CheckKeyContainName,
            final ParseResult    $Result,
            final PTypePackage   $TPackage,
            final CompileProduct $CProduct) {
        final boolean aIsMapPort = CheckIfMapPort(OperandName, $Result, $TPackage, $CProduct);
        if (!aIsMapPort) {
            final int aPosOfAssociateBegin = $Result.startPositionOf(AssociateBeginName);
            $CProduct.reportError(
                    "Map-Port is requried for associate operation. <Util_MapPort:30>",
                    null,
                    aPosOfAssociateBegin);
            return null;
        }
        final Engine  $Engine  = $CProduct.getEngine();
        final Object  aOprRAW  = $Result.valueOf(OperandName, $TPackage, $CProduct);
        
        final TypeRef aMapTRef;
        final Object  aOpr = ((Expression)aOprRAW).getParam(0);
        final boolean aIsGetDHValue =
                       (aOprRAW instanceof Expression)
                    && (((Expression)aOprRAW).isInstruction($Engine, Inst_GetDHValue.Name));
        final boolean aIsGetAttrValue;
        if (aIsGetDHValue) {
            aMapTRef        = $CProduct.getReturnTypeRefOf(aOprRAW);
            aIsGetAttrValue = false;
        } else {
            aMapTRef        = $CProduct.getReturnTypeRefOf(aOprRAW);
            aIsGetAttrValue = ((Expression)aOprRAW).isInstruction($Engine, Inst_GetAttrValue.Name);
            if (!aIsGetAttrValue) {
                $CProduct.reportError(
                        "Only local variable or attribute can be a port. <Util_MapPort:49>",
                        null,
                        $Result.startPositionOf(AssociateBeginName));
                return null;
            }
        }
        
        final TypeRef[] aMapPrmTRefs  = aMapTRef.getParameters($Engine);
        final TypeRef   aMapKeyTRef   = aMapPrmTRefs[0];
        final TypeRef   aMapValueTRef = aMapPrmTRefs[1];
        
        // Check key type
        final Object  aKey             = $Result.valueOf(KeyName, $TPackage, $CProduct);
        final TypeRef aKeyTRef         = $CProduct.getReturnTypeRefOf(aKey);
        final boolean aIsKeyCompatible = aMapKeyTRef.canBeAssignedByInstanceOf($Engine, aKeyTRef);
        if (!aIsKeyCompatible) {
            $CProduct.reportError(
                    String.format(
                        "In compatible map-port key: required `%s` but `%s` found.",
                        aMapKeyTRef,
                        aKeyTRef
                    ),
                    null,
                    $Result.startPositionOf(AssociateBeginName));
            return null;
        }
        
        final MExecutable $ME = $Engine.getExecutableManager();
        
        // TODO - Eliminate this let check as map
        final boolean aIsContainKeyCheck = ($Result.textOf(CheckKeyContainName) != null);
        if (aIsContainKeyCheck) {
            final Coordinate  aAssociateBeginCR = $Result.coordinateOf(AssociateBeginName);
            final Expression  aContainCheckExpr = $ME.newExpr(aAssociateBeginCR, Inst_IsMapPortContainsKey.Name, aOpr, aKey);
            final TypeRef     aResultRef        = $CProduct.getReturnTypeRefOf(aContainCheckExpr);
            if (!TKJava.TBoolean.getTypeRef().equals(aResultRef)) {
                $CProduct.reportError(
                        String.format(
                            "Internal ERROR: " +
                            "In compatible map-port value: required `boolean` but `%s` found.",
                            aResultRef
                        ),
                        null,
                        $Result.startPositionOf(AssociateBeginName));
                return null;
            }
            return aContainCheckExpr;
        }
        
        final Coordinate aAssociateBeginCR = $Result.coordinateOf(AssociateBeginName);
        final Expression aPortExpr;
        
        if (!aIsGetAttrValue)
            aPortExpr = $ME.newExpr(aAssociateBeginCR, Inst_GetMapPortElement.Name, aOpr, aKey);
        else {
            final Object aAttrName = ((Expression)aOprRAW).getParam(1);
            aPortExpr = $ME.newExpr(aAssociateBeginCR, Inst_GetAttrMapPortElement.Name, aOpr, aAttrName, aKey);
        }
        
        final Expression aValueExpr = $ME.newExpr(aAssociateBeginCR, Inst_GetDHValueOrNull .Name, aPortExpr);
        
        // Get Value Type
        final TypeRef aValueTRef         = $CProduct.getReturnTypeRefOf(aValueExpr);
        final boolean aIsValueCompatible = aMapValueTRef.canBeAssignedByInstanceOf($Engine, aValueTRef);
        if (!aIsValueCompatible) {
            $CProduct.reportError(
                    String.format(
                        "Internal ERROR: " +
                        "In compatible map-port value: required `%s` but `%s` found.",
                        aMapValueTRef,
                        aValueTRef
                    ),
                    null,
                    $Result.startPositionOf(AssociateBeginName));
            return null;
        }
        
        return aValueExpr;
    }
    
    static boolean CheckIfMapPort(
            final String         OperandName,
            final ParseResult    $Result,
            final PTypePackage   $TPackage,
            final CompileProduct $CProduct) {
        final Engine  $Engine  = $CProduct.getEngine();
        final Object  aOprRAW  = $Result.valueOf(OperandName, $TPackage, $CProduct);
        final TypeRef aOprTRef = $CProduct.getReturnTypeRefOf(aOprRAW);
        final Object  aOpr     = ((Expression)aOprRAW).getParam(0);
        final TypeRef aMapTRef = $Engine.getTypeManager().getPrefineTypeRef("Map");
        // Check if Map port
        final boolean aIsMap = aMapTRef.canBeAssignedByInstanceOf($Engine, aOprTRef);
        if (!aIsMap)
            return false;
        
        final boolean aIsPort =
               Util_Compiler.IsOperandPort(aOpr,    $CProduct)
            || Util_Compiler.IsOperandPort(aOprRAW, $CProduct);
        return aIsPort;
    }
}
