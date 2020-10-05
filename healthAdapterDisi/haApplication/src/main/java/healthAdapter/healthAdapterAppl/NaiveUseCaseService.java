package healthAdapter.healthAdapterAppl;
import healthAdapter.UseCase;
import healthAdapter.port.in.NaiveUseCase;

@UseCase
public class NaiveUseCaseService implements NaiveUseCase {
	public NaiveUseCaseService() {
		System.out.println("			 %%% NaiveUseCaseService CREATED");
	}
	public String doSomething( String  command) {
		String outS = "NaiveUseCaseService doSomething for input:" + command;
		System.out.println(outS);
		return outS;
	}
}