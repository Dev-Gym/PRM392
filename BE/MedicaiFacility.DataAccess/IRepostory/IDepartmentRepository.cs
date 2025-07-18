using Azure;
using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IDepartmentRepository
    {
        List<Department> GetAllDepartment();
        Department FindById(int id);
        void AddDepartment(Department department);
        void UpdateDepartment(Department department);
        void DeleteDepartment(int id);
        (List<Department>, int totalItem) FindAllWithPagination(int pg, int pageSize);
    }
}
