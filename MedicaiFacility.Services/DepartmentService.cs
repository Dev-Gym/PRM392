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
    public class DepartmentService : IDepartmentService
    {
        private readonly IDepartmentRepository _departmentRepository;

        public DepartmentService(IDepartmentRepository departmentRepository)
        {
            _departmentRepository = departmentRepository;
        }

        public List<Department> GetAllDepartment()
        {
            return _departmentRepository.GetAllDepartment();
        }
        public Department FindById(int id)
        {
            return _departmentRepository.FindById(id);
        }

        public void AddDepartment(Department department)
        {
            _departmentRepository.AddDepartment(department);
        }

        public void UpdateDepartment(Department department)
        {
            _departmentRepository.UpdateDepartment(department);
        }

        public void DeleteDepartment(int id)
        {
            _departmentRepository.DeleteDepartment(id);
        }

        public (List<Department>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            return _departmentRepository.FindAllWithPagination(pg, pageSize);
        }
    }
}
