package tr.tests;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mybean")
public class JsonController {

    private static int counter;

    @RequestMapping(value="/{name}", method=RequestMethod.GET)
    public @ResponseBody MyBean getMyBean(@PathVariable String name) {
        MyBean myBean = new MyBean();
        myBean.setKey1(name);
        myBean.setKey2(counter++);
        return myBean;
    }
    
    @RequestMapping(value="/get-more", method=RequestMethod.GET)
    public @ResponseBody MyBean getAnotherMyBean(@RequestParam("name") String name) {
        MyBean myBean = new MyBean();
        myBean.setKey1(name);
        myBean.setKey2(counter++);
        return myBean;
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/post", consumes="application/json", produces="application/json")
    public @ResponseBody MyBean addMyBean(@RequestBody MyBean input) {
        MyBean myBean = new MyBean();
        myBean.setKey1(input.getKey1());
        myBean.setKey2(-input.getKey2());
        return myBean;
    }
}
