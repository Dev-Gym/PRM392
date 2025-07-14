using Azure;
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
    public class DiseaseRepository : IDiseaseRepository
    {
        private readonly AppDbContext _Context;
        public DiseaseRepository(AppDbContext context)
        {
            _Context = context;
        }
        public List<Disease> GetAllDisease()
        {
            return _Context.Diseases.Include(d => d.Department).ToList();
        }
        public Disease FindById(int id)
        {
            return _Context.Diseases.Find(id);
        }

        public void AddDisease(Disease disease)
        {
            _Context.Diseases.Add(disease);
            _Context.SaveChanges();
        }

        public void UpdateDisease(Disease disease)
        {
            _Context.Diseases.Update(disease);
            _Context.SaveChanges();
        }

        public void DeleteDisease(int id)
        {
            var disease = _Context.Diseases.Find(id);
            if (disease == null)
            {
                throw new InvalidOperationException($"Disease with ID {id} not found.");
            }
            disease.IsActive = false;
            _Context.Diseases.Update(disease);
            _Context.SaveChanges();
        }
        public (List<Disease>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            var query = _Context.Diseases.Include(d => d.Department);

            int totalItem = query.Count();
            var data = query
                .Skip((pg - 1) * pageSize) 
                .Take(pageSize) 
                .ToList();

            return (data, totalItem);
        }
    }
}
