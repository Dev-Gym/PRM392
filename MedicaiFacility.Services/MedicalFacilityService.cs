using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public class MedicalFacilityService : IMedicalFacilityService
    {
        private readonly IMedicalFacilityRepository _medicalFacilityRepository;

        public MedicalFacilityService(IMedicalFacilityRepository medicalFacilityRepository)
        {
            _medicalFacilityRepository = medicalFacilityRepository;
        }

        public List<MedicalFacility> GetAllMedicalFacility()
        {
            return _medicalFacilityRepository.GetAllMedicalFacility();
        }
        public MedicalFacility FindById(int id)
        {
            return _medicalFacilityRepository.FindById(id);
        }

        public void AddMedicalFacility(MedicalFacility medicalFacility)
        {
            _medicalFacilityRepository.AddMedicalFacility(medicalFacility);
        }

        public void UpdateMedicalFacility(MedicalFacility medicalFacility)
        {
            _medicalFacilityRepository.UpdateMedicalFacility(medicalFacility);
        }

        public void DeleteMedicalFacility(int id)
        {
            _medicalFacilityRepository.DeleteMedicalFacility(id);
        }

        public (List<MedicalFacility>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            return _medicalFacilityRepository.FindAllWithPagination(pg, pageSize);
        }

        public (List<MedicalFacility>, Dictionary<int, List<string>>, int totalItem) FindAllWithDepartmentsAndPagination(int pg, int pageSize)
        {
            return _medicalFacilityRepository.FindAllWithDepartmentsAndPagination(pg, pageSize);
        }

        public void UpdateMedicalFacilityWithDepartments(MedicalFacility medicalFacility, List<int> selectedDepartmentIds)
        {
            _medicalFacilityRepository.UpdateMedicalFacilityWithDepartments(medicalFacility, selectedDepartmentIds);
        }
        public List<int?> GetDepartmentIdsByFacilityId(int facilityId)
        {
            return _medicalFacilityRepository.GetDepartmentIdsByFacilityId(facilityId);
        }
    }
}
