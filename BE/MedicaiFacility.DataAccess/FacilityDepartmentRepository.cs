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
    public class FacilityDepartmentRepository : IFacilityDepartmentRepository
    {
        private readonly AppDbContext _Context;
        public FacilityDepartmentRepository(AppDbContext context)
        {
            _Context = context;
        }
        public List<FacilityDepartment> GetAllFacilityDepartment()
        {
            return _Context.FacilityDepartments
                .Include(d => d.Department)
                .Include(mf => mf.Facility)
                .ToList();
        }
        public FacilityDepartment FindById(int id)
        {
            return _Context.FacilityDepartments.Find(id);
        }

        public void AddFacilityDepartment(FacilityDepartment facilityDepartment)
        {
            _Context.FacilityDepartments.Add(facilityDepartment);
            _Context.SaveChanges();
        }

        public void UpdateFacilityDepartment(FacilityDepartment facilityDepartment)
        {
            _Context.FacilityDepartments.Update(facilityDepartment);
            _Context.SaveChanges();
        }

        public void DeleteFacilityDepartment(int id)
        {
            var facilityDepartment = _Context.FacilityDepartments.Find(id);
            if (facilityDepartment == null)
            {
                throw new InvalidOperationException($"Department with ID {id} not found.");
            }


            facilityDepartment.Status = false;
            _Context.FacilityDepartments.Update(facilityDepartment);
            _Context.SaveChanges();
        }

        public (List<FacilityDepartment>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            var query = _Context.FacilityDepartments
                .Include(d => d.Department)
                .Include(mf => mf.Facility)
            .OrderByDescending(d => d.DepartmentId); 

            int totalItem = query.Count();
            var data = query
                .Skip((pg - 1) * pageSize) // Skip records for previous pages
                .Take(pageSize) // Take only the current page's worth
                .ToList();

            return (data, totalItem);
        }
    }
}
