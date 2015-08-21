// youni: dummy implementation
/**
 * This class only take cares of the UI related functions
 *
 * The UI control will get needed info from this UI Kit class
 * youni: move some code in sdkhelper to this class
 * including sync functions
 */
public class EaseUI{
	private static EaseUI instance = null;
	
	private EaseUserProfileProvider profileProvider = null;
	private EaseSettingsProvider settingsProvider = null;
	private EaseNotifier notifier = null;
	private Context context = null;
	private Activity activeActivity = null;
	
	static class EaseUser{
		String hxId = null;
		
		public EaseUser(String id){
			hxId = id;
		}
		
		public String getUserNick(){
			reutrn hxId;
		}
		
		public String getUserAvatar(){
			return "";
		}
	}
	
	static interface EaseUserProfileProvider{
		public EaseUser getUserProfile(String id);
	}
	
	static interface EaseSettingsProvider{
		public boolean isSpeakerAllowed();
		public boolean isMsgNotificationAllowed();
	}
	
	static interface EaseEmojiItem{
		public String getString();
		public int getResourceId();
	}
	
	static interface EaseEmojiPlugin{
		public int count();
		public EaseEmojiItem getEmoji(int index); 
	}
	
	public static getInstance(){
		if(instance == null){
			instance = new EaseUI(); 
		}
		
		return instance;
	}
	
	public setActiveActivit(Activity activity){
		activeActivity = activity;
	}
	
	public boolean hasActiveActivity(){
		return activeActivity != null;
	}
	
	public void init(Context context){
		this.context = context;
	}
	
	public void setUserProfileProvider(EaseUserProfileProvider profileProvider){
		this.profileProvider = profileProvider;
	}
	
	public EaseUserProfileProvider getUserProfileProvider(){
		return profileProvider;
	}
	
	public void setSettingsProvider(EaseSettingsProvider provider){
		settingsProvider = provider;
	}
	
	public EaseSettingsProvider getSettingProvider(){
		return settingsProvider;
	}
	
	public void setNotifier(EaseNotifier notifier){
		this.notifier = notifier;
	}
	
	pubic EaseNotifier getNotifier(){
		if(notifier == null){
			notifier = new EaseNotifier();
		}
		
		return notifier;
	}
}