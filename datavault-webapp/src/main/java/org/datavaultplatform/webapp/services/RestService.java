package org.datavaultplatform.webapp.services;

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
import org.datavaultplatform.webapp.security.EvaluatorService;
import org.springframework.http.ResponseEntity;

public interface RestService extends NotifyLogoutService, NotifyLoginService, EvaluatorService {

  <T> ResponseEntity<T> get(String url, Class<T> clazz);

  <T> ResponseEntity<T> put(String url, Class<T> clazz, Object payload);

  <T> ResponseEntity<T> post(String url, Class<T> clazz, Object payload);

  <T> ResponseEntity<T> delete(String url, Class<T> clazz);

  FileStore[] getFileStoreListing();

  FileStore[] getFileStoresLocal();

  FileStore[] getFileStoresSFTP();

  ArchiveStore[] getArchiveStores();

  ArchiveStore getArchiveStore(String archiveStoreID);

  FileInfo[] getFilesListing(String filePath);

  String getFilesize(String filePath);

  DepositSize checkDepositSize(String[] filePaths);

  VaultInfo[] getVaultsListing();

  VaultInfo[] getVaultsListingForGroup(String groupID);

  VaultInfo[] getVaultsListingAll(String userID);

  BillingInformation getVaultBillingInfo(String vaultId);

  VaultsData getVaultsListingAll(String sort, String order, int offset, int maxResult);

  VaultsData getVaultsForReview();

  ReviewInfo[] getReviewsListing(String vaultId);

  ReviewInfo getCurrentReview(String vaultId);

  VaultReview getVaultReview(String vaultReviewId);

  DepositReview getDepositReview(String depositReviewId);

  VaultsData searchVaultsForBilling(String query, String sort, String order, int offset,
      int maxResult);

  VaultsData searchVaults(String query, String sort, String order, int offset, int maxResult);

  int getVaultsCount();

  Long getVaultsSize();

  int getDepositsCount();

  int getDepositsQueue();

  int getDepositsInProgressCount();

  Deposit[] getDepositsInProgress();

  DepositInfo[] searchDepositsQuery(String query);

  DepositsData searchDepositsData(String query);

  DepositInfo[] searchDeposits(String query, String sort);

  DepositsData searchDepositsData(String query, String sort, String order);

  int getRetrievesCount();

  int getRetrievesQueue();

  int getRetrievesInProgressCount();

  Retrieve[] getRetrievesInProgress();

  Retrieve[] getRetrievesListingAll();

  Vault getVaultRecord(String id);

  VaultInfo getVault(String id);

  Vault checkVaultRetentionPolicy(String vaultId);

  int getRetentionPolicyStatusCount(int status);

  DepositInfo[] getDepositsListing(String vaultId);

  DataManager[] getDataManagers(String vaultId);

  DataManager getDataManager(String vaultId, String uun);

  DepositsData getDepositsListingAllData();

  DepositInfo[] getDepositsListingAll(String query, String sort, String order, int offset,
      int maxResult);

  Integer getTotalDepositsCount(String query);

  DepositsData getDepositsListingAllData(String sort);

  DepositInfo getDeposit(String depositID);

  FileFixity[] getDepositManifest(String depositID);

  EventInfo[] getDepositEvents(String depositID);

  Job[] getDepositJobs(String depositID);

  Retrieve[] getDepositRetrieves(String depositID);

  RetentionPolicy[] getRetentionPolicyListing();

  CreateRetentionPolicy getRetentionPolicy(String retentionPolicyId);

  User getUser(String userId);

  User[] getUsers();

  User[] searchUsers(String query);

  int getUsersCount();

  Group getGroup(String groupId);

  Group[] getGroups();

  Group[] getGroupsByScopedPermissions();

  int getGroupsCount();

  int getGroupVaultCount(String vaultid);

  boolean deleteGroup(String groupID);

  Group updateGroup(Group group);

  Dataset[] getDatasets();

  EventInfo[] getEvents();

  int getEventCount();

  String addFileChunk(String fileUploadHandle, String filename, String encodedRelativePath,
      String chunkNumber, String totalChunks, String chunkSize, String totalSize, byte[] content);

  FileStore addFileStore(FileStore fileStore);

  FileStore addFileStoreSFTP(FileStore fileStore);

  ArchiveStore addArchiveStore(ArchiveStore archiveStore);

  ArchiveStore editArchiveStore(ArchiveStore archiveStore);

  VaultInfo addVault(CreateVault createVault);

  void transferVault(String vaultId, TransferVault transfer);

  ReviewInfo createCurrentReview(String vaultId);

  VaultReview editVaultReview(VaultReview vaultReview);

  DepositInfo addDeposit(CreateDeposit createDeposit);

  DepositReview addDepositReview(String depositId, String vaultReviewId);

  DepositReview editDepositReview(DepositReview depositReview);

  Group addGroup(Group group);

  Boolean retrieveDeposit(String depositID, Retrieve retrieve);

  Deposit restartDeposit(String depositID);

  User addUser(User user);

  VaultInfo addDataManager(String vaultId, String dataManagerUUN);

  VaultInfo deleteDataManager(String vaultId, String dataManagerID);

  User editUser(User user);

  Boolean userExists(ValidateUser validateUser);

  Boolean isValid(ValidateUser validateUser);

  Boolean isAdmin(ValidateUser validateUser);

  String notifyLogin(CreateClientEvent clientEvent);

  String enableGroup(String groupId);

  String disableGroup(String groupId);

  String addGroupOwner(String groupId, String userId);

  /* DELETE requests */
  void removeGroupOwner(String groupId, String userId);

  void deleteFileStore(String fileStoreId);

  void deleteArchiveStore(String archiveStoreId);

  VaultInfo updateVaultDescription(String vaultId, String vaultDescription);

  VaultInfo updateVaultName(String vaultId, String vaultName);

  VaultInfo updateVaultReviewDate(String vaultId, String reviewDate);

  void deleteDeposit(String depositId);

  BillingInformation updateBillingInfo(String vaultId, BillingInformation billingInfo);

  RoleModel createRole(RoleModel role);

  RoleAssignment createRoleAssignment(RoleAssignment roleAssignment);

  List<PermissionModel> getSchoolPermissions();

  List<PermissionModel> getVaultPermissions();

  Optional<RoleModel> getRole(long id);

  RoleModel getIsAdmin();

  List<RoleModel> getEditableRoles();

  List<RoleModel> getViewableRoles();

  List<RoleModel> getSchoolRoles();

  List<RoleModel> getVaultRoles();

  Optional<RoleAssignment> getRoleAssignment(Long id);

  List<RoleAssignment> getRoleAssignmentsForSchool(String schoolId);

  List<RoleAssignment> getRoleAssignmentsForVault(String vaultId);

  List<RoleAssignment> getRoleAssignmentsForUser(String userId);

  List<RoleAssignment> getRoleAssignmentsForRole(long roleId);

  RoleModel updateRole(RoleModel role);

  RoleAssignment updateRoleAssignment(RoleAssignment roleAssignment);

  void deleteRole(Long roleId);

  void deleteRoleAssignment(Long roleAssignmentId);

  String auditDeposits();

  AuditInfo[] getAuditsListingAll();

  EventInfo[] getVaultsRoleEvents(String vaultId);

  void deleteRetentionPolicy(String policyId);

  CreateRetentionPolicy addRetentionPolicy(CreateRetentionPolicy createRetentionPolicy);

  CreateRetentionPolicy editRetentionPolicy(CreateRetentionPolicy createRetentionPolicy);
}
