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
    public class FacilityDepartmentService : IFacilityDepartmentService
    {
        private readonly IFacilityDepartmentRepository _facilityDepartmentRepository;

        public FacilityDepartmentService(IFacilityDepartmentRepository facilityDepartmentRepository)
        {
            _facilityDepartmentRepository = facilityDepartmentRepository;
        }

        public List<FacilityDepartment> GetAllFacilityDepartment()
        {
            return _facilityDepartmentRepository.GetAllFacilityDepartment();
        }
        public FacilityDepartment FindById(int id)
        {
            return _facilityDepartmentRepository.FindById(id);
        }

        public void AddFacilityDepartment(FacilityDepartment facilityDepartment)
        {
            _facilityDepartmentRepository.AddFacilityDepartment(facilityDepartment);
        }

        public void UpdateFacilityDepartment(FacilityDepartment facilityDepartment)
        {
            _facilityDepartmentRepository.UpdateFacilityDepartment(facilityDepartment);
        }

        public void DeleteFacilityDepartment(int id)
        {
            _facilityDepartmentRepository.DeleteFacilityDepartment(id);
        }

        public (List<FacilityDepartment>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            return _facilityDepartmentRepository.FindAllWithPagination(pg, pageSize);
        }
    }
}
