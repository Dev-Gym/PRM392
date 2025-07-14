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
	public class TransactionService : ITransactionService
	{

		private readonly ITransactionRepository _transactionRepository;
        public TransactionService(ITransactionRepository transactionRepository)
        {
            _transactionRepository = transactionRepository;
        }

		public void Create(Transaction transaction)
		{
			_transactionRepository.Create(transaction);
		}

		public void DeleteById(int id)
		{
			_transactionRepository.DeleteById(id);
		}

		public List<Transaction> GetAll()
		{
			return _transactionRepository.GetAll();
		}


        public Transaction GetById(int id)
		{
			return _transactionRepository.GetById(id);
		}

		public (List<Transaction>, int totalItems) GetListByPagination(int pg, int pagesize)
		{
			return _transactionRepository.GetListByPagination(pg, pagesize);
		}

		public (List<Transaction>, int totalItems) GetListByPaginationWithPatientId(int pg, int pageSize, int patientId)
		{
			return _transactionRepository.GetListByPaginationWithPatientId(pg, pageSize,patientId);
		}

		public void Update(Transaction transaction)
		{
			_transactionRepository.Update(transaction);
		}
	}
}
