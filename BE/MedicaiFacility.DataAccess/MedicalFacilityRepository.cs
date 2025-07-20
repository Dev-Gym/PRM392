using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
    public class MedicalFacilityRepository : IMedicalFacilityRepository
    {
        private readonly AppDbContext _Context;
        public MedicalFacilityRepository(AppDbContext context)
        {
            _Context = context;
        }
        public List<MedicalFacility> GetAllMedicalFacility()
        {
            return _Context.MedicalFacilities.Include(x=>x.MedicalExperts).ThenInclude(x=>x.MedicalExpertSchedules)
                .Include(x=>x.FacilityDepartments).ThenInclude(x=>x.Department).ToList();
        }
        public MedicalFacility FindById(int id)
        {
            return _Context.MedicalFacilities.Include(x => x.FacilityDepartments).ThenInclude(x => x.Department).FirstOrDefault(x=>x.FacilityId==id);
        }

        public void AddMedicalFacility(MedicalFacility medicalFacility)
        {
            _Context.MedicalFacilities.Add(medicalFacility);
            _Context.SaveChanges();
        }

        public void UpdateMedicalFacility(MedicalFacility medicalFacility)
        {
            _Context.MedicalFacilities.Update(medicalFacility);
            _Context.SaveChanges();
        }

        public void DeleteMedicalFacility(int id)
        {
            using var transaction = _Context.Database.BeginTransaction();
            try
            {
                // Step 1: Find the medical facility
                var medicalFacility = _Context.MedicalFacilities.Find(id);
                if (medicalFacility == null)
                {
                    throw new InvalidOperationException($"MedicalFacility with ID {id} not found.");
                }

                // Step 2: Set IsActive = false for the medical facility
                medicalFacility.IsActive = false;
                _Context.MedicalFacilities.Update(medicalFacility);

                // Step 3: Find all MedicalExperts associated with this facility
                var medicalExperts = _Context.MedicalExperts
                    .Include(me => me.Expert) // Include the associated User
                    .Where(me => me.FacilityId == id)
                    .ToList();

                // Step 4: Update the Status of each associated MedicalExpert's User to false
                foreach (var expert in medicalExperts)
                {
                    if (expert.Expert != null)
                    {
                        expert.Expert.Status = false;
                        _Context.Users.Update(expert.Expert);
                    }
                }

                // Step 5: Save all changes
                _Context.SaveChanges();
                transaction.Commit();
            }
            catch (Exception ex)
            {
                transaction.Rollback();
                throw new Exception($"Failed to delete MedicalFacility and update associated experts: {ex.Message}", ex);
            }
        }

        public (List<MedicalFacility>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            var query = _Context.MedicalFacilities;
            //.OrderByDescending(d => d.DepartmentId); 

            int totalItem = query.Count();
            var data = query
                .Skip((pg - 1) * pageSize) // Skip records for previous pages
                .Take(pageSize) // Take only the current page's worth
                .ToList();

            return (data, totalItem);
        }
        public (List<MedicalFacility>, Dictionary<int, List<string>>, int totalItem) FindAllWithDepartmentsAndPagination(int pg, int pageSize)
        {
            // Step 1: Fetch paginated medical facilities
            var facilitiesQuery = _Context.MedicalFacilities.OrderByDescending(x => x.FacilityId).ToList();
            int totalItem = facilitiesQuery.Count();

            var facilities = facilitiesQuery
                .Skip((pg - 1) * pageSize)
                .Take(pageSize)
                .ToList();

            // Step 2: Fetch department names for the paginated facilities
            var facilityIds = facilities.Select(f => f.FacilityId).ToList();

            // Join FacilityDepartments with Departments to get department names
            var facilityDepartments = (from fd in _Context.FacilityDepartments
                                       join d in _Context.Departments on fd.DepartmentId equals d.DepartmentId
                                       where facilityIds.Contains(fd.FacilityId ?? 0)
                                       select new
                                       {
                                           fd.FacilityId,
                                           d.DepartmentName
                                       })
                                       .ToList();

            // Step 3: Group department names by FacilityId
            var facilityDepartmentsDict = facilityDepartments
                .GroupBy(fd => fd.FacilityId ?? 0)
                .ToDictionary(
                    g => g.Key,
                    g => g.Select(fd => fd.DepartmentName).ToList()
                );

            // Ensure all facilities have an entry in the dictionary, even if they have no departments
            foreach (var facility in facilities)
            {
                if (!facilityDepartmentsDict.ContainsKey(facility.FacilityId))
                {
                    facilityDepartmentsDict[facility.FacilityId] = new List<string>();
                }
            }

            return (facilities, facilityDepartmentsDict, totalItem);
        }
        public void UpdateMedicalFacilityWithDepartments(MedicalFacility medicalFacility, List<int> selectedDepartmentIds)
        {
            // Step 1: Update the medical facility details
            _Context.MedicalFacilities.Update(medicalFacility);

            // Step 2: Fetch existing FacilityDepartment records for this facility
            var existingFacilityDepartments = _Context.FacilityDepartments
                .Where(fd => fd.FacilityId == medicalFacility.FacilityId)
                .ToList();

            var existingDepartmentIds = existingFacilityDepartments.Select(fd => fd.DepartmentId).ToList();

            // Step 3: Remove departments that are no longer selected
            foreach (var fd in existingFacilityDepartments.Where(fd => !selectedDepartmentIds.Contains(fd.DepartmentId ?? 0)))
            {
                _Context.FacilityDepartments.Remove(fd);
            }

            // Step 4: Add new departments that are selected but not currently associated
            foreach (var departmentId in selectedDepartmentIds.Where(did => !existingDepartmentIds.Contains(did)))
            {
                var facilityDepartment = new FacilityDepartment
                {
                    FacilityId = medicalFacility.FacilityId,
                    DepartmentId = departmentId,
                    CreatedAt = DateTime.Now,
                    Status = true, 
                };
                _Context.FacilityDepartments.Add(facilityDepartment);
            }

            // Step 5: Save all changes in one transaction
            _Context.SaveChanges();
        }
        public List<int?> GetDepartmentIdsByFacilityId(int facilityId)
        {
            return _Context.FacilityDepartments
                .Where(fd => fd.FacilityId == facilityId)
                .Select(fd => fd.DepartmentId)
                .ToList();
        }

        public MedicalFacility FindByExperId(int experId)
        {
            var expert = _Context.MedicalExperts.FirstOrDefault(x => x.ExpertId == experId);
            var item = _Context.MedicalFacilities.FirstOrDefault(x => x.FacilityId == expert.FacilityId);
            return item;
        }
    }
}
