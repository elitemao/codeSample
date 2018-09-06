package textMining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
// MergeHashMap-----------
//v0 20110221: only applicable for HashMap<String,ArrayList>

public class MergeHashMap {

	public static HashMap merge(ArrayList arrayOfHashMap){
		HashMap bigHashMap=new HashMap();
		for(Object i:arrayOfHashMap){
			Set oneKeySet=((HashMap)i).keySet();
			for(Object oneKey:oneKeySet){
				if(bigHashMap.keySet().contains(oneKey)){
					ArrayList array=(ArrayList)(bigHashMap.get(oneKey));
					array.addAll((ArrayList)((HashMap)i).get(oneKey));
					bigHashMap.put(oneKey, array);
				}else{
					bigHashMap.put(oneKey, ((HashMap)i).get(oneKey));
				}
			}
		}
		return bigHashMap;
	}
	
	
	
	
}
