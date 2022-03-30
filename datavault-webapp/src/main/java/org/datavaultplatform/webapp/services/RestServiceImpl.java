package org.datavaultplatform.webapp.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.datavaultplatform.common.model.ArchiveStore;
import org.datavaultplatform.common.model.DataManager;
import org.datavaultplatform.common.model.Dataset;
import org.datavaultplatform.common.model.Deposit;
import org.datavaultplatform.common.model.DepositReview;
import org.datavaultplatform.common.model.FileFixity;
import org.datavaultplatform.common.model.FileInfo;
import org.datavaultplatform.common.model.FileStore;
import org.datavaultplatform.common.model.Group;
import org.datavaultplatform.common.model.Job;
import org.datavaultplatform.common.model.PermissionModel;
import org.datavaultplatform.common.model.RetentionPolicy;
import org.datavaultplatform.common.model.Retrieve;
import org.datavaultplatform.common.model.RoleAssignment;
import org.datavaultplatform.common.model.RoleModel;
import org.datavaultplatform.common.model.User;
import org.datavaultplatform.common.model.Vault;
import org.datavaultplatform.common.model.VaultReview;
import org.datavaultplatform.common.request.CreateClientEvent;
import org.datavaultplatform.common.request.CreateDeposit;
import org.datavaultplatform.common.request.CreateRetentionPolicy;
import org.datavaultplatform.common.request.CreateVault;
import org.datavaultplatform.common.request.TransferVault;
import org.datavaultplatform.common.request.ValidateUser;
import org.datavaultplatform.common.response.AuditInfo;
import org.datavaultplatform.common.response.BillingInformation;
import org.datavaultplatform.common.response.DepositInfo;
import org.datavaultplatform.common.response.DepositSize;
import org.datavaultplatform.common.response.DepositsData;
import org.datavaultplatform.common.response.EventInfo;
import org.datavaultplatform.common.response.ReviewInfo;
import org.datavaultplatform.common.response.VaultInfo;
import org.datavaultplatform.common.response.VaultsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * User: Robin Taylor
 * Date: 01/05/2015
 * Time: 14:04
 */
@Service
@Profile("!standalone")
public class RestServiceImpl implements RestService {

    private static final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);

    private final String brokerURL;
    private final String brokerApiKey;

    @Autowired
    public RestServiceImpl(
        @Value("${broker.url}")String brokerURL,
        @Value("${broker.api.key}") String brokerApiKey) {
        this.brokerURL = brokerURL;
        this.brokerApiKey = brokerApiKey;
    }

    private <T> HttpEntity<T> exchange(String url, Class<T> clazz, HttpMethod method, Object payload) {

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setErrorHandler(new ApiErrorHandler());

        HttpHeaders headers = new HttpHeaders();

        // If we have a logged on user then pass that information.
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set("X-UserID", SecurityContextHolder.getContext().getAuthentication().getName());
        }

        headers.set("X-Client-Key", brokerApiKey);

        HttpEntity entity;
        if (method == HttpMethod.GET) {
            entity = new HttpEntity(headers);
        } else if (method == HttpMethod.PUT) {
            entity = new HttpEntity(payload, headers);
        } else if (method == HttpMethod.POST) {
            entity = new HttpEntity(payload, headers);
        } else if (method == HttpMethod.DELETE) {
            entity = new HttpEntity(headers);
        } else {
            throw new IllegalArgumentException("REST method not implemented!");
        }

        logger.debug("Calling Broker with url:" + url + " Method:" + method);
        System.out.println("Calling Broker with url:" + url + " Method:" + method);

        // todo : check the http status code before returning?

        return restTemplate.exchange(url, method, entity, clazz);

    }

    @Override
    public <T> HttpEntity<T> get(String url, Class<T> clazz) {
        return exchange(url, clazz, HttpMethod.GET, null);
    }

    @Override
    public <T> HttpEntity<T> put(String url, Class<T> clazz, Object payload) {
        return exchange(url, clazz, HttpMethod.PUT, payload);
    }

    @Override
    public <T> HttpEntity<T> post(String url, Class<T> clazz, Object payload) {
        return exchange(url, clazz, HttpMethod.POST, payload);
    }

    @Override
    public HttpEntity<?> delete(String url, Class clazz) {
        return exchange(url, clazz, HttpMethod.DELETE, null);
    }

    /* GET requests */

    @Override
    public FileStore[] getFileStoreListing() {
        HttpEntity<?> response = get(brokerURL + "/filestores", FileStore[].class);
        return (FileStore[])response.getBody();
    }

    @Override
    public FileStore[] getFileStoresLocal() {
        HttpEntity<?> response = get(brokerURL + "/filestores/local", FileStore[].class);
        return (FileStore[])response.getBody();
    }

    @Override
    public FileStore[] getFileStoresSFTP() {
        HttpEntity<?> response = get(brokerURL + "/filestores/sftp", FileStore[].class);
        return (FileStore[])response.getBody();
    }

    @Override
    public ArchiveStore[] getArchiveStores() {
        HttpEntity<?> response = get(brokerURL + "/admin/archivestores", ArchiveStore[].class);
        return (ArchiveStore[])response.getBody();
    }

    @Override
    public ArchiveStore getArchiveStore(String archiveStoreID) {
        HttpEntity<?> response = get(brokerURL + "/admin/archivestores/" + archiveStoreID, ArchiveStore.class);
        return (ArchiveStore)response.getBody();
    }




    @Override
    public FileInfo[] getFilesListing(String filePath) {

        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        HttpEntity<?> response = get(brokerURL + "/files" + filePath, FileInfo[].class);
        return (FileInfo[])response.getBody();
    }

    @Override
    public String getFilesize(String filePath) {

        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        HttpEntity<?> response = get(brokerURL + "/filesize" + filePath, String.class);
        return (String)response.getBody();
    }

    @Override
    public DepositSize checkDepositSize(String[] filePaths) {

        String parameters = "?";

        for (int i=0; i< filePaths.length; i++){
            String filePath = filePaths[i];
            if (!filePath.startsWith("/")) {
                filePath = "/" + filePath;
            }
            parameters += "filepath=" + filePath + "&";
        }

        System.out.println("parameters: " + parameters);

        HttpEntity<?> response = get(brokerURL + "/checkdepositsize" + parameters, DepositSize.class);

        System.out.println("return: " + response.getBody());

        return (DepositSize) response.getBody();
    }

    @Override
    public VaultInfo[] getVaultsListing() {
        HttpEntity<?> response = get(brokerURL + "/vaults", VaultInfo[].class);
        return (VaultInfo[])response.getBody();
    }

    @Override
    public VaultInfo[] getVaultsListingForGroup(String groupID) {
        HttpEntity<?> response = get(brokerURL + "/groups/" + groupID + "/vaults", VaultInfo[].class);
        return (VaultInfo[])response.getBody();
    }
    @Override
    public VaultInfo[] getVaultsListingAll(String userID) {
        HttpEntity<?> response = get(brokerURL + "/vaults/user?userID=" + userID, VaultInfo[].class);
        return (VaultInfo[])response.getBody();
    }

    @Override
    public BillingInformation getVaultBillingInfo(String vaultId) {
        HttpEntity<?> response = get(brokerURL + "/admin/billing/" + vaultId , BillingInformation.class);
        return (BillingInformation)response.getBody();
    }

    @Override
    public VaultsData getVaultsListingAll(String sort, String order, int offset, int maxResult) {
        HttpEntity<?> response = get(brokerURL + "/admin/vaults?sort=" + sort + "&order=" + order+ "&offset=" + offset+ "&maxResult=" + maxResult, VaultsData.class);
        return (VaultsData)response.getBody();
    }

    @Override
    public VaultsData getVaultsForReview() {
        HttpEntity<?> response = get(brokerURL + "/admin/vaultsForReview", VaultsData.class);
        return (VaultsData) response.getBody();
    }

    @Override
    public ReviewInfo[] getReviewsListing(String vaultId) {
        HttpEntity<?> response = get(brokerURL +"/vaults/" + vaultId + "/vaultreviews", ReviewInfo[].class);
        return (ReviewInfo[])response.getBody();
    }

    @Override
    public ReviewInfo getCurrentReview(String vaultId) {
        HttpEntity<?> response = get(brokerURL +"/admin/vaults/" + vaultId + "/vaultreviews/current", ReviewInfo.class);
        return (ReviewInfo)response.getBody();

    }

    @Override
    public VaultReview getVaultReview(String vaultReviewId) {
        HttpEntity<?> response = get(brokerURL + "/vaults/vaultreviews/" + vaultReviewId, VaultReview.class);
        return (VaultReview)response.getBody();
    }

    @Override
    public DepositReview getDepositReview(String depositReviewId) {
        HttpEntity<?> response = get(brokerURL +"/vaultreviews/depositreviews/" +  depositReviewId, DepositReview.class);
        return (DepositReview)response.getBody();
    }

    @Override
    public VaultsData searchVaultsForBilling(String query, String sort, String order, int offset,
        int maxResult) {
        HttpEntity<?> response = get(brokerURL + "/admin/billing/search?query=" + query + "&sort=" + sort + "&order=" + order+ "&offset=" + offset+ "&maxResult=" + maxResult, VaultsData.class);
        return (VaultsData)response.getBody();
    }

    @Override
    public VaultsData searchVaults(String query, String sort, String order, int offset,
        int maxResult) {
        HttpEntity<?> response = get(brokerURL + "/vaults/search?query=" + query + "&sort=" + sort + "&order=" + order+ "&offset=" + offset+ "&maxResult=" + maxResult, VaultsData.class);
        return (VaultsData)response.getBody();
    }

    @Override
    public int getVaultsCount() {
        HttpEntity<?> response = get(brokerURL + "/statistics/count", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public Long getVaultsSize() {
        HttpEntity<?> response = get(brokerURL + "/statistics/size", Long.class);
        return (Long)response.getBody();
    }

    @Override
    public int getDepositsCount() {
        HttpEntity<?> response = get(brokerURL + "/statistics/depositcount", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public int getDepositsQueue() {
        HttpEntity<?> response = get(brokerURL + "/vaults/depositqueuecount", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public int getDepositsInProgressCount() {
        HttpEntity<?> response = get(brokerURL + "/statistics/depositinprogresscount", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public Deposit[] getDepositsInProgress() {
        HttpEntity<?> response = get(brokerURL + "/statistics/depositinprogress", Deposit[].class);
        return (Deposit[])response.getBody();
    }
    @Override
    public DepositInfo[] searchDepositsQuery(String query) {
        HttpEntity<?> response = get(brokerURL + "/vaults/deposits/search/Query?query=" + query, DepositInfo[].class);
        return (DepositInfo[])response.getBody();
    }
    @Override
    public DepositsData searchDepositsData(String query) {
        HttpEntity<?> response = get(brokerURL + "/vaults/deposits/data/search?query=" + query, DepositsData.class);
        return (DepositsData)response.getBody();
    }

    @Override
    public DepositInfo[] searchDeposits(String query, String sort) {
        HttpEntity<?> response = get(brokerURL + "/vaults/deposits/search?query=" + query + "&sort=" + sort, DepositInfo[].class);
        return (DepositInfo[])response.getBody();
    }
    @Override
    public DepositsData searchDepositsData(String query, String sort, String order) {
        HttpEntity<?> response = get(brokerURL + "/vaults/deposits/data/search?query=" + query + "&sort=" + sort + "&order=" + order, DepositsData.class);
        return (DepositsData)response.getBody();
    }

    @Override
    public int getRetrievesCount() {
        HttpEntity<?> response = get(brokerURL + "/statistics/retrievecount", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public int getRetrievesQueue() {
        HttpEntity<?> response = get(brokerURL + "/vaults/retrievequeuecount", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public int getRetrievesInProgressCount() {
        HttpEntity<?> response = get(brokerURL + "/statistics/retrieveinprogresscount", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public Retrieve[] getRetrievesInProgress() {
        HttpEntity<?> response = get(brokerURL + "/vaults/retrieveinprogress", Retrieve[].class);
        return (Retrieve[])response.getBody();
    }

    @Override
    public Retrieve[] getRetrievesListingAll() {
        HttpEntity<?> response = get(brokerURL + "/admin/retrieves", Retrieve[].class);
        return (Retrieve[])response.getBody();
    }

    @Override
    public Vault getVaultRecord(String id) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + id + "/record", Vault.class);
        return (Vault)response.getBody();
    }

    @Override
    public VaultInfo getVault(String id) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + id, VaultInfo.class);
        return (VaultInfo)response.getBody();
    }

    @Override
    public Vault checkVaultRetentionPolicy(String vaultId) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + vaultId + "/checkretentionpolicy", Vault.class);
        return (Vault)response.getBody();
    }

    @Override
    public int getRetentionPolicyStatusCount(int status) {
        HttpEntity<?> response = get(brokerURL + "/vaults/retentionpolicycount/" + status, Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public DepositInfo[] getDepositsListing(String vaultId) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + vaultId + "/deposits", DepositInfo[].class);
        return (DepositInfo[])response.getBody();
    }


    @Override
    public DataManager[] getDataManagers(String vaultId) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + vaultId + "/dataManagers", DataManager[].class);
        return (DataManager[])response.getBody();
    }

    @Override
    public DataManager getDataManager(String vaultId, String uun) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + vaultId + "/dataManager/" + uun, DataManager.class);
        return (DataManager)response.getBody();
    }

    @Override
    public DepositsData getDepositsListingAllData() {
        HttpEntity<?> response = get(brokerURL + "/admin/deposits/data", DepositsData.class);
        return (DepositsData)response.getBody();
    }

    @Override
    public DepositInfo[] getDepositsListingAll(String query, String sort, String order, int offset,
        int maxResult) {
        HttpEntity<?> response = get(brokerURL + "/admin/deposits?query=" + query + "&sort=" + sort + "&order=" + order+ "&offset=" + offset+ "&maxResult=" + maxResult, DepositInfo[].class);
        return (DepositInfo[])response.getBody();
    }

    @Override
    public Integer getTotalDepositsCount(String query) {
        HttpEntity<?> response = get(brokerURL + "/admin/deposits/count?query="+query, Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public DepositsData getDepositsListingAllData(String sort) {
        HttpEntity<?> response = get(brokerURL + "/admin/deposits/data?sort=" + sort, DepositsData.class);
        return (DepositsData)response.getBody();
    }

    @Override
    public DepositInfo getDeposit(String depositID) {
        HttpEntity<?> response = get(brokerURL + "/deposits/" + depositID, DepositInfo.class);
        return (DepositInfo)response.getBody();
    }

    @Override
    public FileFixity[] getDepositManifest(String depositID) {
        HttpEntity<?> response = get(brokerURL + "/deposits/" + depositID + "/manifest", FileFixity[].class);
        return (FileFixity[])response.getBody();
    }

    @Override
    public EventInfo[] getDepositEvents(String depositID) {
        HttpEntity<?> response = get(brokerURL + "/deposits/" + depositID + "/events", EventInfo[].class);
        return (EventInfo[])response.getBody();
    }

    @Override
    public Job[] getDepositJobs(String depositID) {
        HttpEntity<?> response = get(brokerURL + "/deposits/" + depositID + "/jobs", Job[].class);
        return (Job[])response.getBody();
    }

    @Override
    public Retrieve[] getDepositRetrieves(String depositID) {
        HttpEntity<?> response = get(brokerURL + "/deposits/" + depositID + "/retrieves", Retrieve[].class);
        return (Retrieve[])response.getBody();
    }

    @Override
    public RetentionPolicy[] getRetentionPolicyListing() {
        HttpEntity<?> response = get(brokerURL + "/retentionpolicies", RetentionPolicy[].class);
        return (RetentionPolicy[])response.getBody();
    }

    @Override
    public CreateRetentionPolicy getRetentionPolicy(String retentionPolicyId) {
        HttpEntity<?> response = get(brokerURL + "/admin/retentionpolicies/" + retentionPolicyId, CreateRetentionPolicy.class);
        return (CreateRetentionPolicy)response.getBody();
    }

    @Override
    public User getUser(String userId) {
        HttpEntity<?> response = get(brokerURL + "/users/" + userId, User.class);
        return (User)response.getBody();
    }

    @Override
    public User[] getUsers() {
        HttpEntity<?> response = get(brokerURL + "/users", User[].class);
        return (User[])response.getBody();
    }

    @Override
    public User[] searchUsers(String query) {
        HttpEntity<?> response = get(brokerURL + "/admin/users/search?query=" + query, User[].class);
        return (User[])response.getBody();
    }

    @Override
    public int getUsersCount() {
        HttpEntity<?> response = get(brokerURL + "/admin/users/count", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public Group getGroup(String groupId) {
        HttpEntity<?> response = get(brokerURL + "/groups/" + groupId, Group.class);
        return (Group)response.getBody();
    }

    @Override
    public Group[] getGroups() {
        HttpEntity<?> response = get(brokerURL + "/groups", Group[].class);
        return (Group[])response.getBody();
    }

    @Override
    public Group[] getGroupsByScopedPermissions() {
        HttpEntity<?> response = get(brokerURL + "/groups/byScopedPermissions", Group[].class);
        return (Group[])response.getBody();
    }

    @Override
    public int getGroupsCount() {
        HttpEntity<?> response = get(brokerURL + "/groups/count", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public int getGroupVaultCount(String vaultid) {
        HttpEntity<?> response = get(brokerURL + "/groups/" + vaultid + "/count", Integer.class);
        return (Integer)response.getBody();
    }

    @Override
    public boolean deleteGroup(String groupID) {
        HttpEntity<?> response = delete(brokerURL + "/groups/" + groupID, Boolean.class);
        return (Boolean)response.getBody();
    }

    @Override
    public Group updateGroup(Group group) {
        HttpEntity<?> response = post(brokerURL + "/groups/update", Group.class, group);
        return (Group)response.getBody();
    }

    @Override
    public Dataset[] getDatasets() {
        HttpEntity<?> response = get(brokerURL + "/metadata/datasets", Dataset[].class);
        return (Dataset[])response.getBody();
    }

    @Override
    public EventInfo[] getEvents() {
        HttpEntity<?> response = get(brokerURL + "/admin/events?sort=timestamp", EventInfo[].class);
        return (EventInfo[])response.getBody();
    }

    @Override
    public int getEventCount() {
        HttpEntity<?> response = get(brokerURL + "/statistics/eventcount", Integer.class);
        return (Integer)response.getBody();
    }

    /* POST requests */

    @Override
    public String addFileChunk(String fileUploadHandle, String filename, String encodedRelativePath,
        String chunkNumber, String totalChunks, String chunkSize, String totalSize, byte[] content) {

        String fileChunkURL = brokerURL + "/upload/" + fileUploadHandle + "/" + filename + "?" +
                "relativePath=" + encodedRelativePath + "&" +
                "chunkNumber=" + chunkNumber + "&" +
                "totalChunks=" + totalChunks + "&" +
                "chunkSize=" + chunkSize + "&" +
                "totalSize=" + totalSize + "&";

        HttpEntity<?> response = post(fileChunkURL, Byte[].class, content);
        return (String)response.getBody();
    }

    @Override
    public FileStore addFileStore(FileStore fileStore) {
        HttpEntity<?> response = post(brokerURL + "/filestores/", FileStore.class, fileStore);
        return (FileStore)response.getBody();
    }

    @Override
    public FileStore addFileStoreSFTP(FileStore fileStore) {
        HttpEntity<?> response = post(brokerURL + "/filestores/sftp", FileStore.class, fileStore);
        return (FileStore)response.getBody();
    }

    @Override
    public ArchiveStore addArchiveStore(ArchiveStore archiveStore) {
        System.out.println("Post request to broker");
        HttpEntity<?> response = post(brokerURL + "/admin/archivestores/", ArchiveStore.class, archiveStore);
        System.out.println("Done");
        return (ArchiveStore)response.getBody();
    }

    @Override
    public ArchiveStore editArchiveStore(ArchiveStore archiveStore) {
        HttpEntity<?> response = put(brokerURL + "/admin/archivestores/", ArchiveStore.class, archiveStore);
        return (ArchiveStore)response.getBody();
    }

    @Override
    public VaultInfo addVault(CreateVault createVault) {
        HttpEntity<?> response = post(brokerURL + "/vaults/", VaultInfo.class, createVault);
        return (VaultInfo)response.getBody();
    }

    @Override
    public void transferVault(String vaultId, TransferVault transfer) {
        post(brokerURL + "/vaults/" + vaultId + "/transfer", VaultInfo.class, transfer);
    }

    @Override
    public ReviewInfo createCurrentReview(String vaultId) {
        HttpEntity<?> response = post(brokerURL +"/admin/vaults/vaultreviews/current", ReviewInfo.class,  vaultId);
        return (ReviewInfo)response.getBody();
    }

    @Override
    public VaultReview editVaultReview(VaultReview vaultReview) {
        HttpEntity<?> response = put(brokerURL + "/admin/vaults/vaultreviews", VaultReview.class, vaultReview);
        return (VaultReview)response.getBody();
    }

    @Override
    public DepositInfo addDeposit(CreateDeposit createDeposit) {
        HttpEntity<?> response = post(brokerURL + "/deposits", DepositInfo.class, createDeposit);
        return (DepositInfo)response.getBody();
    }

    @Override
    public DepositReview addDepositReview(String depositId, String vaultReviewId) {
        HttpEntity<?> response = post(brokerURL +"/admin/vaultreviews/" + vaultReviewId +  "/depositreviews", DepositReview.class, depositId);
        return (DepositReview)response.getBody();
    }

    @Override
    public DepositReview editDepositReview(DepositReview depositReview) {
        HttpEntity<?> response = put(brokerURL + "/admin/vaultreviews/depositreviews", DepositReview.class, depositReview);
        return (DepositReview)response.getBody();
    }


    @Override
    public Group addGroup(Group group) {
        HttpEntity<?> response = post(brokerURL + "/groups/", Group.class, group);
        return (Group)response.getBody();
    }

    @Override
    public Boolean retrieveDeposit(String depositID, Retrieve retrieve) {
        HttpEntity<?> response = post(brokerURL + "/deposits/" + depositID + "/retrieve", Boolean.class, retrieve);
        return (Boolean)response.getBody();
    }

    @Override
    public Deposit restartDeposit(String depositID) {
        HttpEntity<?> response = post(brokerURL + "/deposits/" + depositID + "/restart", Deposit.class, null);
        return (Deposit)response.getBody();
    }

    @Override
    public User addUser(User user) {
        HttpEntity<?> response = post(brokerURL + "/users/", User.class, user);
        return (User)response.getBody();
    }

    @Override
    public VaultInfo addDataManager(String vaultId, String dataManagerUUN) {
        HttpEntity<?> response = post(brokerURL + "/vaults/" + vaultId + "/addDataManager", VaultInfo.class, dataManagerUUN);
        return (VaultInfo)response.getBody();
    }

    @Override
    public VaultInfo deleteDataManager(String vaultId, String dataManagerID) {
        HttpEntity<?> response = delete(brokerURL + "/vaults/" + vaultId + "/deleteDataManager/" + dataManagerID, VaultInfo.class);
        return (VaultInfo)response.getBody();
    }

    @Override
    public User editUser(User user) {
        HttpEntity<?> response = put(brokerURL + "/admin/users/", User.class, user);
        return (User)response.getBody();
    }

    @Override
    public Boolean userExists(ValidateUser validateUser) {
        // Using a POST because I read somewhere that its somehow more secure to POST credentials, could be nonsense
        HttpEntity<?> response = post(brokerURL + "/auth/users/exists", Boolean.class, validateUser);
        return (Boolean)response.getBody();
    }

    @Override
    public Boolean isValid(ValidateUser validateUser) {
        // Using a POST because I read somewhere that its somehow more secure to POST credentials, could be nonsense
        HttpEntity<?> response = post(brokerURL + "/auth/users/isvalid", Boolean.class, validateUser);
        return (Boolean)response.getBody();
    }

    @Override
    public Boolean isAdmin(ValidateUser validateUser) {
        // Using a POST because I read somewhere that its somehow more secure to POST credentials, could be nonsense
        HttpEntity<?> response = post(brokerURL + "/auth/users/isadmin", Boolean.class, validateUser);
        return (Boolean)response.getBody();
    }

    @Override
    public String notifyLogin(CreateClientEvent clientEvent) {
        HttpEntity<?> response = put(brokerURL + "/notify/login", String.class, clientEvent);
        return (String)response.getBody();
    }

    @Override
    public String notifyLogout(CreateClientEvent clientEvent) {
        HttpEntity<?> response = put(brokerURL + "/notify/logout", String.class, clientEvent);
        return (String)response.getBody();
    }

    @Override
    public String enableGroup(String groupId) {
        HttpEntity<?> response = put(brokerURL + "/groups/" + groupId + "/enable", String.class, null);
        return (String)response.getBody();
    }

    @Override
    public String disableGroup(String groupId) {
        HttpEntity<?> response = put(brokerURL + "/groups/" + groupId + "/disable", String.class, null);
        return (String)response.getBody();
    }

    @Override
    public String addGroupOwner(String groupId, String userId) {
        HttpEntity<?> response = put(brokerURL + "/groups/" + groupId + "/users/" + userId, String.class, null);
        return (String)response.getBody();
    }

    /* DELETE requests */
    @Override
    public void removeGroupOwner(String groupId, String userId) {
        delete(brokerURL + "/groups/" + groupId + "/users/" + userId, String.class);
    }

    @Override
    public void deleteFileStore(String fileStoreId) {
        delete(brokerURL + "/filestores/" + fileStoreId, String.class);
    }

    @Override
    public void deleteArchiveStore(String archiveStoreId) {
        delete(brokerURL + "/admin/archivestores/" + archiveStoreId, String.class);
    }

    @Override
    public VaultInfo updateVaultDescription(String vaultId, String vaultDescription) {
        HttpEntity<?> response = post(brokerURL + "/vaults/" + vaultId + "/updateVaultDescription", VaultInfo.class, vaultDescription);
        return (VaultInfo)response.getBody();
    }

    @Override
    public VaultInfo updateVaultName(String vaultId, String vaultName) {
        HttpEntity<?> response = post(brokerURL + "/vaults/" + vaultId + "/updateVaultName", VaultInfo.class, vaultName);
        return (VaultInfo)response.getBody();
    }

    @Override
    public VaultInfo updateVaultReviewDate(String vaultId, String reviewDate) {
        HttpEntity<?> response = post(brokerURL + "/vaults/" + vaultId + "/updatereviewdate", VaultInfo.class, reviewDate);
        return (VaultInfo)response.getBody();
    }

    @Override
    public void deleteDeposit(String depositId) {
        delete(brokerURL + "/admin/deposits/" + depositId, String.class);
    }

    @Override
    public BillingInformation updateBillingInfo(String vaultId, BillingInformation billingInfo) {
        HttpEntity<?> response = post(brokerURL + "/admin/billing/" + vaultId+ "/updateBilling" , BillingInformation.class,billingInfo);
        return (BillingInformation)response.getBody();
    }

    @Override
    public RoleModel createRole(RoleModel role) {
        return post(brokerURL + "/permissions/role", RoleModel.class, role).getBody();
    }

    @Override
    public RoleAssignment createRoleAssignment(RoleAssignment roleAssignment) {
        return post(brokerURL + "/permissions/roleAssignment", RoleAssignment.class, roleAssignment).getBody();
    }

    @Override
    public List<PermissionModel> getSchoolPermissions() {
        return Arrays.asList(get(brokerURL + "/permissions/school", PermissionModel[].class).getBody());
    }

    @Override
    public List<PermissionModel> getVaultPermissions() {
        return Arrays.asList(get(brokerURL + "/permissions/vault", PermissionModel[].class).getBody());
    }

    @Override
    public Optional<RoleModel> getRole(long id) {
        return Optional.ofNullable(get(brokerURL + "/permissions/role/" + id, RoleModel.class).getBody());
    }

    @Override
    public RoleModel getIsAdmin() {
        return get(brokerURL + "/permissions/role/isAdmin", RoleModel.class).getBody();
    }

    @Override
    public List<RoleModel> getEditableRoles() {
        return Arrays.asList(get(brokerURL + "/permissions/roles", RoleModel[].class).getBody());
    }

    @Override
    public List<RoleModel> getViewableRoles() {
        return Arrays.asList(get(brokerURL + "/permissions/roles/readOnly", RoleModel[].class).getBody());
    }

    @Override
    public List<RoleModel> getSchoolRoles() {
        return Arrays.asList(get(brokerURL + "/permissions/roles/school", RoleModel[].class).getBody());
    }

    @Override
    public List<RoleModel> getVaultRoles() {
        return Arrays.asList(get(brokerURL + "/permissions/roles/vault", RoleModel[].class).getBody());
    }

    @Override
    public Optional<RoleAssignment> getRoleAssignment(Long id) {
        return Optional.ofNullable(get(brokerURL + "/permissions/roleAssignment/" + id, RoleAssignment.class).getBody());
    }

    @Override
    public List<RoleAssignment> getRoleAssignmentsForSchool(String schoolId) {
        return Arrays.asList(get(brokerURL + "/permissions/roleAssignments/school/" + schoolId, RoleAssignment[].class).getBody());
    }

    @Override
    public List<RoleAssignment> getRoleAssignmentsForVault(String vaultId) {
        return Arrays.asList(get(brokerURL + "/permissions/roleAssignments/vault/" + vaultId, RoleAssignment[].class).getBody());
    }

    @Override
    public List<RoleAssignment> getRoleAssignmentsForUser(String userId) {
        return Arrays.asList(get(brokerURL + "/permissions/roleAssignments/user/" + userId, RoleAssignment[].class).getBody());
    }

    @Override
    public List<RoleAssignment> getRoleAssignmentsForRole(long roleId) {
        return Arrays.asList(get(brokerURL + "/permissions/roleAssignments/role/" + roleId, RoleAssignment[].class).getBody());
    }

    @Override
    public RoleModel updateRole(RoleModel role) {
        return put(brokerURL + "/permissions/role", RoleModel.class, role).getBody();
    }

    @Override
    public RoleAssignment updateRoleAssignment(RoleAssignment roleAssignment) {
        return put(brokerURL + "/permissions/roleAssignment", RoleAssignment.class, roleAssignment).getBody();
    }

    @Override
    public void deleteRole(Long roleId) {
        delete(brokerURL + "/permissions/role/" + roleId, Void.class);
    }

    @Override
    public void deleteRoleAssignment(Long roleAssignmentId) {
        delete(brokerURL + "/permissions/roleAssignment/" + roleAssignmentId, Void.class);
    }

    @Override
    public String auditDeposits() {
        HttpEntity<?> response =  get(brokerURL + "/admin/deposits/audit", String.class);
        return (String)response.getBody();
    }

    @Override
    public AuditInfo[] getAuditsListingAll() {
        HttpEntity<?> response = get(brokerURL + "/admin/audits", AuditInfo[].class);
        return (AuditInfo[])response.getBody();
    }

    @Override
    public EventInfo[] getVaultsRoleEvents(String vaultId) {
        HttpEntity<?> response = get(brokerURL + "/vaults/" + vaultId + "/roleEvents", EventInfo[].class);
        return (EventInfo[])response.getBody();
    }

    @Override
    public void deleteRetentionPolicy(String policyId) {
        HttpEntity<?> response = delete(brokerURL + "/admin/retentionpolicies/delete/" + policyId, Void.class);
    }

    @Override
    public CreateRetentionPolicy addRetentionPolicy(CreateRetentionPolicy createRetentionPolicy) {
        HttpEntity<?> response = post(brokerURL + "/admin/retentionpolicies", CreateRetentionPolicy.class, createRetentionPolicy);
        return (CreateRetentionPolicy)response.getBody();
    }

    @Override
    public CreateRetentionPolicy editRetentionPolicy(CreateRetentionPolicy createRetentionPolicy) {
        HttpEntity<?> response = put(brokerURL + "/admin/retentionpolicies", CreateRetentionPolicy.class, createRetentionPolicy);
        return (CreateRetentionPolicy)response.getBody();
    }

}
