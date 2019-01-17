package topo.common;

/**
 * Created by gy on 2018/5/6.
 */
public interface TopoEnum {

   public enum CanvasType{

       CANVAS_BUSINESS("business"),

       CANVAS_WHOLE_TOPO("wholetopo"),

       CANVAS_CUSTOM_TOPO("customtopo");

       private String value;

       CanvasType(String name) {
           this.value = name;
       }
       public String value() {
           return this.value;
       }
   }
}
