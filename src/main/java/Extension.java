import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

@SuppressWarnings("unused")
public class Extension implements BurpExtension
{
    @Override
    public void initialize(MontoyaApi montoyaApi)
    {
        montoyaApi.extension().setName("Delete Bytes");

        montoyaApi.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(montoyaApi.logging(), montoyaApi.userInterface().swingUtils().suiteFrame()));
    }
}