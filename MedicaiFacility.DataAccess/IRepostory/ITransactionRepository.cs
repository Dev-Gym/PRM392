using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
	public interface  ITransactionRepository
	{
		List<Transaction> GetAll();
		Transaction GetById(int id);
		void Create(Transaction transaction);
		void Update(Transaction transaction);
		void DeleteById(int id);
		(List<Transaction>, int totalItems) GetListByPagination(int pg, int pagesize);
		(List<Transaction>, int totalItems) GetListByPaginationWithPatientId(int pg, int pageSize, int patientId);


    }
}
