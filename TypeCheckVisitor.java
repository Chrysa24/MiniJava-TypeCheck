import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;  
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TypeCheckVisitor extends  GJDepthFirst<String,GJDepthFirst>{
    
  boolean errorflag = false;
  String classname = null, functionname = null;
  String type1=null;
  int   length=0;
//  int[] param_counter, new_param_counter;
  String[] fun_call=null,class_call=null, prev_fun_call=null, prev_class_call=null;
  int [] param_counter =null;
  int [] new_param_counter  =null;
  int[] sum_args = null, prev_sum_args=null;
     
  
  
    public String visit(Goal n,GJDepthFirst argu) {
     
      DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
      if(visitor1.errorflag==true){
         n.f2.accept(this, argu);
        return null;
      }
      
      n.f0.accept(this, argu);
      if(errorflag==true){
          System.out.println("ERROR");
          return null;
      }
      n.f1.accept(this, argu);
      if(errorflag==true){
          System.out.println("ERROR");
          return null;
      }
      PrintOffsets(visitor1);
    
      n.f2.accept(this, argu);
        return null;
    }
    
    public void PrintOffsets(DefCollectVisitor visitor){
                
        visitor.offsets.entrySet().forEach( entry -> {
            ArrayList list = (ArrayList)entry.getValue();
            HashMap map0 = (HashMap)list.get(0);
            HashMap map1 = (HashMap)list.get(1);
            String Nclass = entry.getKey();
            Iterator iterator0 = map0.entrySet().iterator();
            Iterator iterator1 = map1.entrySet().iterator();
            Map.Entry entry0,entry1;
            
            while(iterator0.hasNext()){
                entry0 = (Map.Entry)iterator0.next();
                System.out.println(Nclass + "." + entry0.getKey() + ": " + entry0.getValue());
            }
            while(iterator1.hasNext()){
                entry1 = (Map.Entry)iterator1.next();
                System.out.println(Nclass + "." + entry1.getKey() + ": " + entry1.getValue());
            }
            
        });
    }
    
   
   public String visit(MainClass n, GJDepthFirst argu) {
      String _ret=null;
      classname =  n.f1.accept(this, argu);
 
      n.f11.accept(this, argu);
   
      n.f14.accept(this, argu);
      if(errorflag==true)
          return null;
      n.f15.accept(this, argu);
    
      classname = null;
      return _ret;
   }
    
   
   public String visit(ClassDeclaration n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String _ret=null;
      String a = n.f0.accept(this, argu);
      String id = n.f1.accept(this, argu);
      classname = id;
      n.f3.accept(this, argu);
      if(errorflag==true)
          return null;
      n.f4.accept(this, argu);
    
      classname = null;
      return _ret;
   }
   
   
   public String visit(ClassExtendsDeclaration n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String _ret=null;
     
      String name = n.f1.accept(this, argu);
      classname = name;
      n.f3.accept(this, argu);
     
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
     
      classname = null;
      return _ret;
   }
    
   
   public String visit(MethodDeclaration n, GJDepthFirst argu) {
       DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
       if(errorflag==true)
           return null;
      String _ret=null;
     
      String return_type = n.f1.accept(this, argu);
      String name = n.f2.accept(this, argu);
      functionname = name;
      n.f4.accept(this, argu);
     
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
    
      n.f10.accept(this, argu);
      functionname = null;
      
      if(type1 != return_type){
           String cur_classname = type1;
           
           while((cur_classname = visitor1.extend_classes.get(cur_classname)) !=null){
                       if(cur_classname == return_type)
                           return _ret;
           }
           
          errorflag=true;
      }
      
      return _ret;
   }
   
   
   public String visit(NodeToken n, GJDepthFirst argu) { return n.toString(); }

    
    public void Typecheck(String type,GJDepthFirst argu){
        if(errorflag==true)
           return;
        DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
        if("int".equals(type) || "boolean".equals(type) ||  "int[]".equals(type) ||   "boolean[]".equals(type) ||   "class".equals(type) ||   "function".equals(type))
            return;
        else{
             ListIterator<List> Tableitr=visitor1.SymbolTable.listIterator();
            ListIterator  listitr=null;
            Object extend_vars =  null;
         
            while(Tableitr.hasNext()) {
               
               listitr=Tableitr.next().listIterator();
               while(listitr.hasNext()) {

                    HashMap next_map = (HashMap)listitr.next();
                    extend_vars = next_map.get(type);                  //klasi
                    if(extend_vars !=null){
                        type1="class";
                        return;
                    }
               }
            }
            errorflag=true;
            return;
        }
    }
    
    
   public String visit(VarDeclaration n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String _ret=null;
      String type = n.f0.accept(this, argu);
      Typecheck(type, argu);
      if(errorflag==true)
          return null;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }
   
   
   public String visit(FormalParameter n, GJDepthFirst argu) {
      String _ret=null;
      String type =n.f0.accept(this, argu);
      Typecheck(type, argu);
      if(errorflag==true)
          return null;
      n.f1.accept(this, argu);
      return _ret;
   }
   
    
   
   public String visit(IntegerLiteral n, GJDepthFirst argu) {
      
      type1 = "int";
      return n.f0.accept(this, argu);
   }
   
   
  
   public String visit(Identifier n, GJDepthFirst argu) {
       
       String cur_classname;
     
       cur_classname= classname;
       DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
      String id = n.f0.accept(this, argu);
      if(errorflag==true)
           return id;
      
       //elegxos gia desmevmenes lekseis
      if (id == "for" || id=="do" || id == "char" || id == "short" || id == "double" || id == "long" || id=="float" || id=="byte" || id=="switch" || id=="case" || id=="default" || id=="break" || id=="continue"){                
          errorflag=true;
           return id;
      }
      
      ListIterator<List> Tableitr=visitor1.SymbolTable.listIterator();
      ListIterator  listitr=null;
      Object extend_vars =  null, fun_vars=null;
      if(id ==null)
          return null;
      
      while(Tableitr.hasNext()) {
           listitr=Tableitr.next().listIterator();
           while(listitr.hasNext()) {
               
               HashMap next_map = (HashMap)listitr.next();
               if(cur_classname == null && (next_map.get(id) !=null)){
                       type1 = id;
                    return id;
               }
               extend_vars = next_map.get(cur_classname);                  //klasi
               if(cur_classname != null && extend_vars != null){
                    if(cur_classname == visitor1.Mainclass ){
                         if(extend_vars !=null && ((HashMap)extend_vars).get(id)!=null){
                             type1 = (String)((HashMap)extend_vars).get(id);
                             return id;
                         }
                    }
                    else {
                             
                       
                             if (functionname !=null){
                                 boolean flag=false;
                                 String type_flag=null;
                                 if(((HashMap)extend_vars).get(id) !=null){         //metabliti pou einai field tis klasis sthn opoia anikei h function
                                     
                                     flag=true;
                                     type_flag = (String)((HashMap)extend_vars).get(id);
                                 }
                                  ListIterator<List> Tableitr2=visitor1.SymbolTable.listIterator();
                                  while(Tableitr2.hasNext()) {
         
                                        listitr=Tableitr2.next().listIterator();
                                       while(listitr.hasNext()) {

                                          next_map = (HashMap)listitr.next();
                                   
                                          fun_vars = next_map.get(functionname);
                              
                                          if(fun_vars != null &&  ((HashMap)fun_vars).get(id) !=null){                //einai metabliti mias synartisis
                                      
                                              type1 = (String)((HashMap)fun_vars).get(id);
                                              return id;
                                          }
                                       }
                                  }
                                 if(flag==true){
                                     type1 = type_flag;
                                     return id;
                                 }
                             }
                             else if(functionname == null &&  ((HashMap)extend_vars).get(id) !=null){            //einai field tis klasis
                                     type1 = (String)((HashMap)extend_vars).get(id);
                                     return id;
                             }
                             
                    }
               }
           }
          
      }
      //mporei na einai onoma klasis
    
          
            Tableitr=visitor1.SymbolTable.listIterator();
            while(Tableitr.hasNext()) {
                listitr=Tableitr.next().listIterator();

                        HashMap next_map = (HashMap)listitr.next();
                       
                        if (next_map.get(id) != null){
                              type1 = id;
                            return id;
                          }
                         while(listitr.hasNext()){
                             next_map = (HashMap)listitr.next();
                             if (next_map.get(id) != null){
                                type1 = "function";
                                return id;
                            }
                         }


            }
                                                                                                        //den einai pedio aytis ths klasis psaxnoume gia yperklasi
           if(visitor1.extend_classes.get(cur_classname) != null){      // h klasi exei yperklasi
                                     
                while((cur_classname = visitor1.extend_classes.get(cur_classname)) !=null){
                       Tableitr=visitor1.SymbolTable.listIterator();
                       while(Tableitr.hasNext()) {
                           
                            listitr=Tableitr.next().listIterator();
                            HashMap next_map = (HashMap)listitr.next();
                            extend_vars = next_map.get(cur_classname);
                             
                            if(extend_vars !=null && ((HashMap)extend_vars).get(id)!=null){
                                type1 = (String)((HashMap)extend_vars).get(id);
                                return id;
                            }
                      }
                }
           }
           else{              // h klasi den exei yperklasi ara den yparxei ayth h metabliti
               errorflag = true;
               type1 = null;
               return id;
           }
                             
       
       type1 = null;
      return id;
   }
   
 
   
   public String visit(CompareExpression n, GJDepthFirst argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      type1=null;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      type1 = "boolean";
      return _ret;
   }

 
   public String visit(PlusExpression n, GJDepthFirst argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      type1=null;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      return _ret;
   }

   
   public String visit(MinusExpression n, GJDepthFirst argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      type1 = null;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      return _ret;
   }
   
   
   public String visit(TimesExpression n, GJDepthFirst argu) {
      String _ret=null;
      String id1 = n.f0.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
      type1 = null;
      n.f1.accept(this, argu);
      String id2 = n.f2.accept(this, argu);
       if(type1!="int"){
          errorflag=true;
          return null;
      }
      
      return _ret;
   }
   
   
   
   public String visit(ArrayLookup n, GJDepthFirst argu) {
      String _ret=null;
      n.f0.accept(this, argu);
     if(type1!="int[]" && type1 !="boolean[]"){
          errorflag=true;
          return null;
      }
     String type = type1;
     type1=null;
      n.f2.accept(this, argu);
    if(type1!="int"){
          errorflag=true;
          return null;
      }
    if(type == "int[]")
        type1 = "int";
    else
        type1 = "boolean";
    
   
      return _ret;
   }
    
   
   public String visit(ArrayLength n, GJDepthFirst argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      if(type1!="int[]" && type1 != "boolean[]"){
          errorflag=true;
          return null;
      }
      type1 = "int";
      return _ret;
   }
  
   
   public String visit(MessageSend n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String _ret=null;
      String id = n.f0.accept(this, argu);
      if(id == "this"){
          id = classname; // the global var
          type1 = "class";
      }
      String nameclass,namefunction;
      if(!"class".equals(type1) ){
          nameclass= new String(type1);
          Typecheck(type1, argu);
          if(type1!="class" ){
                errorflag=true;
                return null;
          }
      }
      else
        nameclass= new String(id);  
      type1=null;
      namefunction = n.f2.accept(this, argu);
     
    
      
    if(length==0){
        length=1;
        new_param_counter = new int[length];
        fun_call= new String[length];
        class_call = new String[length];
        fun_call[length-1] = namefunction;
        class_call[length-1] = nameclass;
        new_param_counter[length-1] = -1;
        sum_args = new int[length];
        sum_args[length-1] = 0;
    }
    else{
        param_counter = new int[length];
        for(int i = 0; i < length ; i++){
            param_counter[i] = new_param_counter[i];
        }
        
        prev_fun_call = new String[length];
        for(int i = 0; i < length ; i++){
            prev_fun_call[i] = fun_call[i];
        }
        
        prev_class_call = new String[length];
        for(int i = 0; i < length ; i++){
            prev_class_call[i] = class_call[i];
        }
        
        prev_sum_args = new int[length];
        for(int i = 0; i < length ; i++){
            prev_sum_args[i] = sum_args[i];
        }
        
        length++;
        
        new_param_counter = new int[length];
        for(int i = 0; i < (length-1) ; i++){               //ta antigrafw ola ektos apo tin teleytaia thesi
            new_param_counter[i] = param_counter[i];
        }
        new_param_counter[length-1] = -1;
      
       
        fun_call = new String[length];
        for(int i = 0; i < (length-1) ; i++){               //ta antigrafw ola ektos apo tin teleytaia thesi
            fun_call[i] = prev_fun_call[i];
        }
        fun_call[length-1] = namefunction;
        
      
        class_call = new String[length];
        for(int i = 0; i < (length-1) ; i++){               //ta antigrafw ola ektos apo tin teleytaia thesi
            class_call[i] = prev_class_call[i];
        }
        class_call[length-1] = nameclass;
        
        sum_args = new int[length];
        for(int i = 0; i < (length-1) ; i++){               //ta antigrafw ola ektos apo tin teleytaia thesi
            sum_args[i] = prev_sum_args[i];
        }
        sum_args[length-1] = -1;
    }
    
    
     
      String return_type = ExistanceCheck(argu, nameclass, namefunction);   //metraw posa args prepei na exei kai poion typo epistrofis exei
      
        n.f4.accept(this, argu);
      
        if(errorflag==true)
           return null;
  
        if(sum_args[length-1] ==0 && new_param_counter[length-1] == -1){
            
        }
        else if(sum_args[length-1] != new_param_counter[length-1]){
            errorflag=true;
            return null;
        }
      
        length--;
        if(length==0){
            new_param_counter=param_counter =sum_args= prev_sum_args= null;
            fun_call= prev_fun_call = class_call = prev_class_call=null;
        }
        else{
            new_param_counter = new int[length];
            for(int i = 0; i < length ; i++){
                new_param_counter[i] = param_counter[i];
            }
            
            fun_call = new String[length];
            for(int i = 0; i < length ; i++){
                fun_call[i] = prev_fun_call[i];
            }
            
            class_call = new String[length];
            for(int i = 0; i < length ; i++){
                class_call[i] = prev_class_call[i];
            }
            
            sum_args = new int[length];
            for(int i = 0; i < length ; i++){
                sum_args[i] = prev_sum_args[i];
            }
        }
      
      
      type1 = return_type;
      return _ret;
   }
   
   public String ExistanceCheck(GJDepthFirst argu, String nameclass,String  namefunction){
            DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
            ListIterator<List> Tableitr=visitor1.SymbolTable.listIterator();
            ListIterator  listitr=null;
            Object extend_vars =  null,fun_vars=null;
            sum_args[length-1] =0;
            
            while(nameclass != null){
                
                Tableitr=visitor1.SymbolTable.listIterator();
                while(Tableitr.hasNext()) {

                   listitr=Tableitr.next().listIterator();
                   while(listitr.hasNext()) {

                        HashMap next_map = (HashMap)listitr.next();
                        extend_vars = next_map.get(nameclass);                  //class
                        if(extend_vars !=null){
                            while(listitr.hasNext()) {
                                    next_map = (HashMap)listitr.next();
                                    
                                    fun_vars = next_map.get(namefunction);                  //function
                                    if(fun_vars!=null){                                          // h function einai member tis klasis
                                        
                                        while(((HashMap)fun_vars).get("chrysa_arg" + String.valueOf(sum_args[length-1])) != null){
                                            sum_args[length-1]++;
                                        }
                                        return (String)((HashMap)fun_vars).get("return_value");     // ton typo epistrofis ths synarthshs
                                    }
                            }
                        }
                        break;
                   }
                }
                nameclass = visitor1.extend_classes.get(nameclass);
            }
            errorflag = true;
            return null;
    }
   
   
   public String Argument_check(GJDepthFirst argu){
       if(errorflag==true)
           return null;
      DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
      ListIterator<List> Tableitr=visitor1.SymbolTable.listIterator();
      ListIterator  listitr=null;
      Object extend_vars =  null,fun_vars=null;
      String nameclass = class_call[length-1];
      String arg_type = null;
      
       while(nameclass != null && arg_type==null){
                
            Tableitr=visitor1.SymbolTable.listIterator();
            while(Tableitr.hasNext()) {

                   listitr=Tableitr.next().listIterator();
                   while(listitr.hasNext()) {

                        HashMap next_map = (HashMap)listitr.next();
                        extend_vars = next_map.get(nameclass);                  //class
                        if(extend_vars !=null){
                            
                            while(listitr.hasNext()) {
                                    next_map = (HashMap)listitr.next();
                                    
                                    fun_vars = next_map.get(fun_call[length-1]);                  //function
                                    if(fun_vars!=null){                                                  // h function einai member tis klasis
                                       
                                       arg_type = (String)((HashMap)fun_vars).get("chrysa_arg"+String.valueOf(new_param_counter[length-1]));     // to 1o arg ths synarthshs
                                       return arg_type;
                                    }
                            }
                        }
                        break;
                }
            }
            nameclass = visitor1.extend_classes.get(nameclass);
       }
       return null;
   }
   
    public String visit(Expression n, GJDepthFirst argu) {
        if(errorflag==true)
                return null;
        
        String exp = n.f0.accept(this, argu);
 

        return exp;
   }
    
    
    
   public String visit(ExpressionList n, GJDepthFirst argu) {
       
       if(errorflag==true)
           return null;
       if(sum_args[length-1]==0){
           return null;
       }
       String _ret=null;
       new_param_counter[length-1] = 0;
      String exp = n.f0.accept(this, argu);
   
        if(new_param_counter!=null   && new_param_counter[length-1] > -1){
            String param_type = Argument_check(argu);
            DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
            
            if( param_type != type1){
                
                String cur_classname = type1;
           
                while((cur_classname = visitor1.extend_classes.get(cur_classname)) !=null){
                       if(cur_classname == param_type){
                           new_param_counter[length-1] ++;
                           return exp;
                       }
                }
              
               errorflag = true;
               return null;
           }
            new_param_counter[length-1] ++;
        }
      
   
       n.f1.accept(this, argu);
      return _ret;
   }

  
   
   public String visit(ExpressionTail n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      return n.f0.accept(this, argu);
   }

  
   public String visit(ExpressionTerm n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String _ret=null;
      n.f0.accept(this, argu);
      String exp = n.f1.accept(this, argu);
      
        if(new_param_counter!=null   && new_param_counter[length-1] > -1){
            String param_type = Argument_check(argu);
            DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
            
            if( param_type != type1){
                
                String cur_classname = type1;
           
                while((cur_classname = visitor1.extend_classes.get(cur_classname)) !=null){
                       if(cur_classname == param_type){
                           new_param_counter[length-1] ++;
                           return exp;
                       }
                }
               errorflag = true;
               return null;
           }
            new_param_counter[length-1] ++;
        }
      return _ret;
   }
   
   
   public String visit(BooleanArrayAllocationExpression n, GJDepthFirst argu) {
      String _ret=null;
      if(errorflag==true)
           return null;
      n.f3.accept(this, argu);
      if(type1!="int"){
          errorflag=true;
          return null;
      }
     type1 = "boolean[]";
      return _ret;
   }

   
   public String visit(IntegerArrayAllocationExpression n, GJDepthFirst argu) {
      String _ret=null;
    
      n.f3.accept(this, argu);
     if(type1!="int"){
          errorflag=true;
          return null;
      }
     type1 = "int[]";
      return _ret;
   }
   
   
   public String visit(AllocationExpression n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
      String _ret=null;
      String id = n.f1.accept(this, argu);
      String id_type = type1;
      if(type1!="class"){
          Typecheck(type1, argu);
          if(type1 != "class"){
                errorflag=true;
                return null;
          }
          else
              type1 = id_type;
      }
      return id;
   }
   
   public String visit(BooleanArrayType n, GJDepthFirst argu) {
        type1 = "boolean[]";
        return "boolean[]";
   }
    
    public String visit(IntegerArrayType n, GJDepthFirst argu) {
         type1 = "int[]";
      return "int[]";
   }
   
    
   public String visit(BooleanType n, GJDepthFirst argu) {
       type1 = "boolean";
      return n.f0.accept(this, argu);
   }

   
   public String visit(IntegerType n, GJDepthFirst argu) {
       type1 = "int";
      return n.f0.accept(this, argu);
   }
   
   
   public String visit(Type n, GJDepthFirst argu) {
       DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
       String type = n.f0.accept(this, argu);
       if(errorflag==true)
           return type;
      
      if(type1 == "int" || type1 == "boolean" || type1 == "int[]" || type1 == "boolean[]" || type1 == "class" || type1 == "function" )
          return type;
      else if((type1 == "String[]") && (classname == visitor1.Mainclass))
          return type;
      else{
            
            ListIterator<List> Tableitr=visitor1.SymbolTable.listIterator();
            ListIterator  listitr=null;
            
            while(Tableitr.hasNext()) {
                listitr=Tableitr.next().listIterator();

                 HashMap next_map = (HashMap)listitr.next();
                       
                 if (next_map.get(type1) != null){
                       type1 = "class";
                        return type;
                 }
            }
            errorflag=true;
            return type;
      }
   }
   
   
   public String visit(TrueLiteral n, GJDepthFirst argu) {
       type1 ="boolean";
      return n.f0.accept(this, argu);
   }

   
   public String visit(FalseLiteral n, GJDepthFirst argu) {
        type1 ="boolean";
      return n.f0.accept(this, argu);
   }
   
   
   public String visit(NotExpression n, GJDepthFirst argu) {
      if(errorflag==true)
           return null;
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      if(type1 != "boolean"){
          errorflag=true;
      }
     type1 = "boolean";
      return _ret;
   }
   
   
   public String visit(Clause n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String clause = n.f0.accept(this, argu);
   
      return clause;
   }
   
   
   public String visit(IfStatement n, GJDepthFirst argu) {
      String _ret=null;
      if(errorflag==true)
          return null;
      n.f2.accept(this, argu);
      if(type1 != "boolean"){
          errorflag=true;
          return null;
      }
      n.f4.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
   }
   
   
   public String visit(WhileStatement n, GJDepthFirst argu) {
      String _ret=null;
      if(errorflag==true)
          return null;
      n.f2.accept(this, argu);
      if(type1 != "boolean"){
          errorflag=true;
          return null;
      }
     
      n.f4.accept(this, argu);
      return _ret;
   }
   
   
   public String visit(PrintStatement n, GJDepthFirst argu) {
      String _ret=null;
      if(errorflag==true)
          return null;
      n.f2.accept(this, argu);
      if(type1 != "int"){
          errorflag=true;
          return null;
      }
      return _ret;
   }
   
   
   public String visit(AndExpression n, GJDepthFirst argu) {
      String _ret=null;
      if(errorflag==true)
          return null;
      n.f0.accept(this, argu);
      if(type1 != "boolean"){
          errorflag=true;
          return null;
      }
      n.f2.accept(this, argu);
      if(type1 != "boolean"){
          errorflag=true;
          return null;
      }
      return _ret;
   }
   
  
   
   public String visit(AssignmentStatement n, GJDepthFirst argu) {
       DefCollectVisitor visitor1 = (DefCollectVisitor)argu;
      if(errorflag==true)
           return null;
      String _ret=null;
      String id = n.f0.accept(this, argu);
      String id_type = type1;
      n.f2.accept(this, argu);
      if(type1 != id_type){
          String cur_classname = type1;
           
           while((cur_classname = visitor1.extend_classes.get(cur_classname)) !=null){
                       if(cur_classname == id_type){
                           return null;
                       }
                }
          errorflag=true;
          return null;
      }
      return _ret;
   }

   
   public String visit(ArrayAssignmentStatement n, GJDepthFirst argu) {
       if(errorflag==true)
           return null;
      String _ret=null;
      String id =n.f0.accept(this, argu);
      String id_type = type1;
      n.f2.accept(this, argu);
      if(type1 != "int"){
          errorflag=true;
          return null;
      }
      n.f5.accept(this, argu);
      if(id_type == "int[]")
          id_type = "int";
      else if(id_type == "boolean[]")
          id_type = "boolean";
      else{
          errorflag=true;
          return null;
      }
     if(type1 != id_type){
          errorflag=true;
          return null;
      }
      return _ret;
   }
   
    public String visit(PrimaryExpression n, GJDepthFirst argu) {
      String exp= n.f0.accept(this, argu);
      if(exp == "this"){
          exp = classname;
          type1 = classname;
      }
      return exp;
   }
    
    
   public String visit(BracketExpression n, GJDepthFirst argu) {
      String _ret=null;
      n.f0.accept(this, argu);
     
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
     
      return _ret;
   }
}