package com.tsspdcl.sas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SasApplication {

	/*@Autowired	
	private MessageSource messageSource;
		
	@Autowired
	UserRepository userRepository;*/
	 
	public static void main(String[] args) {
		SpringApplication.run(SasApplication.class, args);
	}
	
	/*@PostConstruct
    public void postConstruct() {
        System.out.println("Running Message Property Data");
        System.out.println(messageSource.getMessage("dashboard.nsts.title", null, Locale.ENGLISH));
        
        System.out.println("End Message Property Data");
    }
	
	@PostConstruct
	public void postConstruct() {
		List<User> users = new ArrayList<>();
		
		users = userRepository.findBySasusernameContainingIgnoreCase("CGM");
	    show(users);
	}
	
	private void show(List<User> users) {
	    users.forEach(System.out::println);
	}
	private NstsDAO nstsDAO;
	
	@PostConstruct
	public void postConstruct() {
		List<NewRegistrations> regList = new ArrayList<>();
		
		regList = nstsDAO.findAllNewRegistrations();
	    show(regList);
	}
	
	private void show(List<NewRegistrations> regList) {
		regList.forEach(System.out::println);
	}
	*/
}
