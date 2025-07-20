using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service.IService
{
    public interface IMedicalFacilityService
    {
        List<MedicalFacility> GetAllMedicalFacility();
        MedicalFacility FindById(int id);
        void AddMedicalFacility(MedicalFacility medicalFacility);
        void UpdateMedicalFacility(MedicalFacility medicalFacility);
        void DeleteMedicalFacility(int id);
        (List<MedicalFacility>, int totalItem) FindAllWithPagination(int pg, int pageSize);
        (List<MedicalFacility>, Dictionary<int, List<string>>, int totalItem) FindAllWithDepartmentsAndPagination(int pg, int pageSize);
        void UpdateMedicalFacilityWithDepartments(MedicalFacility medicalFacility, List<int> selectedDepartmentIds);
        List<int?> GetDepartmentIdsByFacilityId(int facilityId);
        MedicalFacility FindByExperId(int experId);
    }
}
