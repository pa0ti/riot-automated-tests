package mobilestests_android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.esotericsoftware.yamlbeans.YamlException;

import pom_android.main_tabs.RiotFavouritesTabPageObjects;
import pom_android.main_tabs.RiotHomePageTabObjects;
import pom_android.main_tabs.RiotPeopleTabPageObjects;
import pom_android.main_tabs.RiotRoomsTabPageObjects;
import utility.Constant;
import utility.HttpsRequestsToMatrix;
import utility.MatrixUtilities;
import utility.RiotParentTest;
import utility.ScreenshotUtility;

/**
 * Tests on Ux Rework.
 * @author jeangb
 */
@Listeners({ ScreenshotUtility.class })
public class RiotUxReworkTests extends RiotParentTest{
	private String riotUserADisplayName="riotuser1";
	private String riotUserBDisplayName="riotuser2";
	private String riotUserAAccessToken;
	private String testRoomId;
	
	/**
	 * 1. Create room A from home page. </br>
	 * 2. Make it favourite. </br>
	 * Check that the room is in the FAVOURITES section on the home page. </br>
	 * 3. Hit FAVOURITES tab, and check that room A is present. </br>
	 * 4. Hit PEOPLE tab, and check that room A is not present. </br>
	 * 5. Hit ROOMS tab and check that room A is present.
	 * @throws IOException, InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"}, priority=0)
	public void duplicatedFavouritedRoomAccrossTabsTest() throws IOException, InterruptedException{
		String roomName="favouriteRoomAutoTest";
		//1. Create room A from home page.
		createRoomWithByRequestsToMatrix(roomName);
		
		//2. Make it favourite. 
		RiotHomePageTabObjects homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		homePage.clickOnContextMenuOnRoom(roomName, "Favourite");
		//Check that the room is in the FAVOURITES section on the home page. 
		homePage.checkRoomInCategory(roomName, homePage.favouritesSectionLayout,0);
		
		//3. Hit FAVOURITES tab, and check that room A is present.
		RiotFavouritesTabPageObjects favouritesPage=homePage.openFavouriteTab();
		Assert.assertNotNull(favouritesPage.getRoomByName(roomName), "Room "+roomName+" not found in Favourites tab.");
		
		//4. Hit PEOPLE tab, and check that room A is not present.
		RiotPeopleTabPageObjects peopleTab=favouritesPage.openPeopleTab();
		Assert.assertNull(peopleTab.getRoomByName(roomName), "Room "+roomName+" found in People tab and it shouldn't be.");
		
		//5. Hit ROOMS tab and check that room A is present.
		RiotRoomsTabPageObjects roomsTab = peopleTab.openRoomsTab();
		Assert.assertNotNull(roomsTab.getRoomByName(roomName), "Room "+roomName+" not found in Rooms tab.");
	}
	
	/**
	 * 1. Create direct chat with user B. </br>
	 * 2. Make it favourite. </br>
	 * Check that the room is in the FAVOURITES section on the home page. </br>
	 * 3. Hit FAVOURITES tab, and check that direct chat with user B is present. </br>
	 * 4. Hit PEOPLE tab and check that direct chat with user B is present.
	 * 5. Hit ROOMS tab and check that room A is not present.
	 * @throws IOException, InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"}, priority=1)
	public void duplicatedDirectChatAccrossTabsTest() throws IOException, InterruptedException{
		//1. Create direct chat with user B.
		createDirectChatWithByRequestsToMatrix(MatrixUtilities.getMatrixIdFromDisplayName(riotUserBDisplayName));
		
		//2. Make it favourite.
		RiotHomePageTabObjects homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		homePage.clickOnContextMenuOnRoom(riotUserBDisplayName, "Direct Chat");
		homePage.clickOnContextMenuOnRoom(riotUserBDisplayName, "Favourite");
		//Check that the room is in the FAVOURITES section on the home page.
		homePage.checkRoomInCategory(riotUserBDisplayName, homePage.favouritesSectionLayout,0);
		
		//3. Hit FAVOURITES tab, and check that direct chat with user B is present. 
		RiotFavouritesTabPageObjects favouritesPage=homePage.openFavouriteTab();
		Assert.assertNotNull(favouritesPage.getRoomByName(riotUserBDisplayName), "Direct chat "+riotUserBDisplayName+" not found in Favourites tab.");
		
		//4. Hit PEOPLE tab and check that direct chat with user B is present.
		RiotPeopleTabPageObjects peopleTab = favouritesPage.openPeopleTab();
		Assert.assertNotNull(peopleTab.getRoomByName(riotUserBDisplayName), "Direct chat "+riotUserBDisplayName+" not found in People tab.");
		
		//5. Hit ROOMS tab and check that direct chat with user B is not present.
		RiotRoomsTabPageObjects roomsTab = peopleTab.openRoomsTab();
		Assert.assertNull(roomsTab.getRoomByName(riotUserBDisplayName), "Direct chat "+riotUserBDisplayName+" found in Rooms tab and it shouldn't be.");
	}
	
	/**
	 * 1. Create room A from home page. </br>
	 * 2. Make it low priority. </br>
	 * Check that the room is in the LOW PRIORITY section on the home page. </br>
	 * 3. Hit ROOMS tab and check that room A is not present.
	 * @throws IOException, InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"}, priority=2)
	public void notDuplicatedLowPriorityRoomAccrossTabsTest() throws IOException, InterruptedException{
		String roomName = "lowPriorityRoomTest";
		//1. Create room A from home page.
		createRoomWithByRequestsToMatrix(roomName);
		
		//2. Make it low priority. 
		RiotHomePageTabObjects homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		homePage.clickOnContextMenuOnRoom(roomName, "De-prioritize");
		//Check that the room is in the LOW PRIORITY section on the home page. 
		homePage.checkRoomInCategory(roomName, homePage.lowPrioritySectionLayout,0);
		
		//3. Hit ROOMS tab and check that room A is not present
		RiotRoomsTabPageObjects roomsTab=homePage.openRoomsTab();
		Assert.assertNull(roomsTab.getRoomByName(roomName), "Room "+roomName+" found in Rooms tab and it shouldn't be.");
	}

	/**
	 * 1. Create direct chat with user B. </br>
	 * 2. Make it low priority. </br>
	 * Check that the room is in the LOW PRIORITY section on the home page. </br>
	 * 3. Hit PEOPLE tab and check that direct chat with user B is not present.
	 * @throws IOException, InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"}, priority=3)
	public void notDuplicatedDirectChatAccrossTabsTest() throws IOException, InterruptedException{
		//1. Create direct chat with user B.
		createDirectChatWithByRequestsToMatrix(MatrixUtilities.getMatrixIdFromDisplayName(riotUserBDisplayName));
		
		//2. Make it low priority.
		RiotHomePageTabObjects homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		homePage.clickOnContextMenuOnRoom(riotUserBDisplayName, "Direct Chat");
		homePage.clickOnContextMenuOnRoom(riotUserBDisplayName, "De-prioritize");
		//Check that the room is in the LOW PRIORITY section on the home page. 
		homePage.checkRoomInCategory(riotUserBDisplayName, homePage.lowPrioritySectionLayout,0);
		
		//3. Hit PEOPLE tab and check that direct chat with user B is not present.
		RiotPeopleTabPageObjects peopleTab = homePage.openPeopleTab();
		Assert.assertNull(peopleTab.getRoomByName(riotUserBDisplayName), "Direct chat "+riotUserBDisplayName+" found in People tab and it shouldn't be.");
	}
	
	/**
	 * Create a room by using https requests to home server.
	 * @param roomName
	 * @throws IOException 
	 */
	private void createRoomWithByRequestsToMatrix(String roomName) throws IOException{
		//1. Create room R with user A.
		riotUserAAccessToken=HttpsRequestsToMatrix.login(riotUserADisplayName, Constant.DEFAULT_USERPWD);
		testRoomId=HttpsRequestsToMatrix.createRoom(riotUserAAccessToken, roomName);
		//2. Invite user B. 
		HttpsRequestsToMatrix.sendInvitationToUser(riotUserAAccessToken, testRoomId, MatrixUtilities.getMatrixIdFromDisplayName(riotUserBDisplayName));
	}
	
	/**
	 * Create a direct chatwith user B by using https requests to home server.
	 * @param roomName
	 * @throws IOException 
	 */
	private void createDirectChatWithByRequestsToMatrix(String invitedMatrixId) throws IOException{
		//1. Create room R with user A.
		riotUserAAccessToken=HttpsRequestsToMatrix.login(riotUserADisplayName, Constant.DEFAULT_USERPWD);
		testRoomId=HttpsRequestsToMatrix.createDirectChatRoom(riotUserAAccessToken, invitedMatrixId);
		//2. Invite user B. 
		HttpsRequestsToMatrix.sendInvitationToUser(riotUserAAccessToken, testRoomId, MatrixUtilities.getMatrixIdFromDisplayName(riotUserBDisplayName));
	}
	
	@AfterMethod(alwaysRun=true)
	private void leaveRoomAfterTest(Method m) throws InterruptedException, IOException{
		switch (m.getName()) {
		case "duplicatedFavouritedRoomAccrossTabsTest":
			leaveAndForgetRoomUsers();
			break;
		case "duplicatedDirectChatAccrossTabsTest":
			leaveAndForgetRoomUsers();
			break;
		case "notDuplicatedLowPriorityRoomAccrossTabsTest":
			leaveAndForgetRoomUsers();
			break;
		case "notDuplicatedDirectChatAccrossTabsTest":
			leaveAndForgetRoomUsers();
			break;
		default:
			break;
		}
	}
	private void leaveAndForgetRoomUsers() throws IOException{
		//leave room user A
		HttpsRequestsToMatrix.leaveRoom(riotUserAAccessToken, testRoomId);
		//forget room user A
		HttpsRequestsToMatrix.forgetRoom(riotUserAAccessToken, testRoomId);
	}
	/**
	 * Log the good user if not.</br> Secure the test.
	 * @param myDriver
	 * @param username
	 * @param pwd
	 * @throws InterruptedException 
	 * @throws YamlException 
	 * @throws FileNotFoundException 
	 */
	@BeforeGroups("1checkuser")
	private void checkIfUserLogged() throws InterruptedException, FileNotFoundException, YamlException{
		super.checkIfUserLoggedAndHomeServerSetUpAndroid(appiumFactory.getAndroidDriver1(), riotUserADisplayName, Constant.DEFAULT_USERPWD);
	}
}